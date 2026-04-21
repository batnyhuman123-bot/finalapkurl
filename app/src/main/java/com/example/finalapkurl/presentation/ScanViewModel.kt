package com.example.finalapkurl.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalapkurl.data.ScanOutcome
import com.example.finalapkurl.data.ScanRepository
import com.example.finalapkurl.data.local.ScanHistoryRecord
import com.example.finalapkurl.data.local.toScanOutcome
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(
    private val repository: ScanRepository
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _statusMessage = MutableStateFlow("Checking existing reports...")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _result = MutableStateFlow<ScanOutcome?>(null)
    val result: StateFlow<ScanOutcome?> = _result.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _scanKindLabel = MutableStateFlow("URL")
    val scanKindLabel: StateFlow<String> = _scanKindLabel.asStateFlow()

    /** Increments on each prepare so Scanning screen re-runs startScan (e.g. new deep link while already on Scanning). */
    private val _scanSession = MutableStateFlow(0)
    val scanSession: StateFlow<Int> = _scanSession.asStateFlow()

    private val _navigateToResult = MutableSharedFlow<Long>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigateToResult: SharedFlow<Long> = _navigateToResult.asSharedFlow()

    private var pendingUrl: String? = null
    private var pendingUri: Uri? = null

    private var scanJob: Job? = null

    fun prepareUrlScan(url: String) {
        scanJob?.cancel()
        scanJob = null
        pendingUrl = url.trim()
        pendingUri = null
        _scanKindLabel.value = "URL"
        _scanSession.value += 1
    }

    fun prepareApkScan(uri: Uri) {
        scanJob?.cancel()
        scanJob = null
        pendingUri = uri
        pendingUrl = null
        _scanKindLabel.value = "APK"
        _scanSession.value += 1
    }

    fun resetSession() {
        scanJob?.cancel()
        scanJob = null
        pendingUrl = null
        pendingUri = null
        _progress.value = 0f
        _statusMessage.value = "Checking existing reports..."
        _result.value = null
        _error.value = null
        _scanKindLabel.value = "URL"
    }

    fun clearError() {
        _error.value = null
    }

    fun consumeResult() {
        _result.value = null
    }

    fun openHistoryResult(record: ScanHistoryRecord) {
        _result.value = record.toScanOutcome()
    }

    /** Returns true if [ScanOutcome] is available (loaded or already set). */
    suspend fun resolveHistoryIntoResult(scanId: Long): Boolean {
        if (scanId <= 0L) return false
        if (_result.value != null) return true
        val r = repository.getHistoryRecord(scanId) ?: return false
        _result.value = r.toScanOutcome()
        return true
    }

    fun startScan() {
        if (scanJob?.isActive == true) return
        Log.d(
            "VT_CALL",
            "startScan() session=${_scanSession.value} hasUrl=${!pendingUrl.isNullOrBlank()} hasApk=${pendingUri != null}"
        )
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("VT_CALL", "Calling VirusTotal API (repository scan)")
                withContext(Dispatchers.Main.immediate) {
                    _error.value = null
                    _result.value = null
                    _progress.value = 0f
                    _statusMessage.value = "Checking existing reports..."
                }
                val outcome = when {
                    !pendingUrl.isNullOrBlank() ->
                        repository.scanUrl(pendingUrl!!) { p, m ->
                            withContext(Dispatchers.Main.immediate) {
                                _statusMessage.value = m
                                _progress.value = p.coerceIn(0f, 1f)
                            }
                        }

                    pendingUri != null ->
                        repository.scanApk(pendingUri!!) { p, m ->
                            withContext(Dispatchers.Main.immediate) {
                                _statusMessage.value = m
                                _progress.value = p.coerceIn(0f, 1f)
                            }
                        }

                    else -> throw IllegalStateException("Nothing to scan.")
                }
                if (outcome.isFallback) {
                    Log.d("VT_FALLBACK", "Using fallback outcome; still saving to history.")
                }
                val id = repository.insertHistory(outcome)
                withContext(Dispatchers.Main.immediate) {
                    _result.value = outcome
                    _navigateToResult.emit(id)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                withContext(Dispatchers.Main.immediate) {
                    _error.value = e.message ?: "Scan failed."
                }
            }
        }
    }

    companion object {
        fun factory(repository: ScanRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ScanViewModel(repository) as T
                }
            }
    }
}
