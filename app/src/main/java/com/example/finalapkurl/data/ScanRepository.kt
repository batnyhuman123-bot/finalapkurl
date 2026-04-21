package com.example.finalapkurl.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.finalapkurl.data.local.HistoryRepository
import com.example.finalapkurl.data.local.ScanHistoryRecord
import com.example.finalapkurl.data.remote.AnalysisStats
import com.example.finalapkurl.data.remote.VirusTotalApi
import com.example.finalapkurl.data.remote.apkMultipartPart
import com.example.finalapkurl.data.remote.getAnalysisOrNull
import com.example.finalapkurl.data.remote.getFileReportStats
import com.example.finalapkurl.data.remote.getUrlReportStats
import com.example.finalapkurl.domain.RiskMapper
import com.example.finalapkurl.domain.StaticAnalysis
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class ScanRepository(
    private val appContext: Context,
    private val api: VirusTotalApi,
    private val historyRepository: HistoryRepository,
    private val virusTotalApiKey: String
) {

    companion object {
        private const val POLL_INTERVAL_MS = 5000L
        private const val MAX_POLL_ATTEMPTS = 11
    }

    suspend fun scanUrl(
        rawUrl: String,
        onProgress: suspend (Float, String) -> Unit
    ): ScanOutcome = withContext(Dispatchers.IO) {
        val normalized = try {
            normalizeUrl(rawUrl)
        } catch (_: Exception) {
            rawUrl.trim()
        }
        if (virusTotalApiKey.isBlank()) {
            Log.d("VT_FALLBACK", "Empty API key — local fallback for URL")
            onProgress(0.1f, "Checking existing reports...")
            delay(90)
            onProgress(0.38f, "Analyzing results...")
            delay(90)
            onProgress(0.62f, "Analyzing results...")
            delay(90)
            onProgress(0.82f, "Analyzing results...")
            delay(90)
            onProgress(0.94f, "Analyzing results...")
            delay(70)
            onProgress(1f, "Analyzing results...")
            return@withContext buildFallbackUrl(normalized)
        }
        try {
            scanUrlInternal(rawUrl, onProgress)
        } catch (e: CancellationException) {
            throw e
        } catch (ex: Exception) {
            Log.d("VT_FALLBACK", "URL scan failed, using fallback: ${ex.message}")
            onProgress(0.48f, "Analyzing results...")
            delay(100)
            onProgress(0.68f, "Analyzing results...")
            delay(100)
            onProgress(0.86f, "Analyzing results...")
            delay(90)
            onProgress(0.96f, "Analyzing results...")
            delay(70)
            onProgress(1f, "Analyzing results...")
            buildFallbackUrl(normalized)
        }
    }

    suspend fun scanApk(
        uri: Uri,
        onProgress: suspend (Float, String) -> Unit
    ): ScanOutcome = withContext(Dispatchers.IO) {
        val fileName = resolveDisplayName(uri) ?: "file.apk"
        if (virusTotalApiKey.isBlank()) {
            Log.d("VT_FALLBACK", "Empty API key — local fallback for APK")
            onProgress(0.1f, "Checking existing reports...")
            delay(90)
            onProgress(0.38f, "Analyzing results...")
            delay(90)
            onProgress(0.62f, "Analyzing results...")
            delay(90)
            onProgress(0.82f, "Analyzing results...")
            delay(90)
            onProgress(0.94f, "Analyzing results...")
            delay(70)
            onProgress(1f, "Analyzing results...")
            return@withContext buildFallbackApk(uri, fileName)
        }
        try {
            scanApkInternal(uri, onProgress)
        } catch (e: CancellationException) {
            throw e
        } catch (ex: Exception) {
            Log.d("VT_FALLBACK", "APK scan failed, using fallback: ${ex.message}")
            onProgress(0.48f, "Analyzing results...")
            delay(100)
            onProgress(0.68f, "Analyzing results...")
            delay(100)
            onProgress(0.86f, "Analyzing results...")
            delay(90)
            onProgress(0.96f, "Analyzing results...")
            delay(70)
            onProgress(1f, "Analyzing results...")
            buildFallbackApk(uri, fileName)
        }
    }

    private suspend fun scanUrlInternal(
        rawUrl: String,
        onProgress: suspend (Float, String) -> Unit
    ): ScanOutcome {
        Log.d("VT_CALL", "Calling VirusTotal API (URL report / submit)")
        val normalized = normalizeUrl(rawUrl)
        val urlId = UrlCodec.urlToVtId(normalized)

        onProgress(0.08f, "Checking existing reports...")
        val cachedUrlStats: AnalysisStats? = api.getUrlReportStats(urlId)
        if (cachedUrlStats != null && RiskMapper.totalEngines(cachedUrlStats) > 0) {
            onProgress(0.52f, "Analyzing results...")
            delay(95)
            onProgress(0.68f, "Analyzing results...")
            delay(95)
            onProgress(0.82f, "Analyzing results...")
            delay(90)
            onProgress(0.92f, "Analyzing results...")
            delay(85)
            onProgress(0.98f, "Analyzing results...")
            delay(75)
            onProgress(1f, "Analyzing results...")
            return buildOutcomeUrl(normalized, cachedUrlStats)
        }

        onProgress(0.22f, "Submitting for analysis...")
        val submit = api.submitUrl(normalized)
        val analysisId = submit.submitData?.id
            ?: throw IllegalStateException("Could not submit URL for analysis.")

        onProgress(0.32f, "Submitting for analysis...")
        delay(80)

        val stats = pollUntilDone(
            analysisId = analysisId,
            onProgress = onProgress,
            refetch = { api.getUrlReportStats(urlId) }
        )
        onProgress(1f, "Analyzing results...")
        return buildOutcomeUrl(normalized, stats)
    }

    private suspend fun scanApkInternal(
        uri: Uri,
        onProgress: suspend (Float, String) -> Unit
    ): ScanOutcome {
        Log.d("VT_CALL", "Calling VirusTotal API (file report / upload)")
        val fileName = resolveDisplayName(uri) ?: "file.apk"
        val bytes = appContext.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("Cannot read APK file.")

        if (bytes.size > 32 * 1024 * 1024) {
            throw IllegalStateException("File is too large for scanning (max 32 MB).")
        }

        val hashHex = sha256Hex(bytes)
        val hashShort = if (hashHex.length >= 12) {
            "${hashHex.take(6).uppercase()}...${hashHex.takeLast(6).uppercase()}"
        } else {
            hashHex.uppercase()
        }

        onProgress(0.08f, "Checking existing reports...")
        val statsCached: AnalysisStats? = api.getFileReportStats(hashHex)
        if (statsCached != null && RiskMapper.totalEngines(statsCached) > 0) {
                onProgress(0.52f, "Analyzing results...")
                delay(95)
                onProgress(0.68f, "Analyzing results...")
                delay(95)
                onProgress(0.82f, "Analyzing results...")
                delay(90)
                onProgress(0.92f, "Analyzing results...")
                delay(85)
                onProgress(0.98f, "Analyzing results...")
                delay(75)
                onProgress(1f, "Analyzing results...")
                return buildOutcomeApk(fileName, hashShort, statsCached)
        }

        onProgress(0.22f, "Submitting for analysis...")
        val part = apkMultipartPart(fileName, bytes)
        val submit = api.uploadFile(part)
        val analysisId = submit.submitData?.id
            ?: throw IllegalStateException("Could not submit file for analysis.")

        onProgress(0.32f, "Submitting for analysis...")
        delay(80)

        val stats = pollUntilDone(
            analysisId = analysisId,
            onProgress = onProgress,
            refetch = { api.getFileReportStats(hashHex) }
        )
        onProgress(1f, "Analyzing results...")
        return buildOutcomeApk(fileName, hashShort, stats)
    }

    suspend fun insertHistory(outcome: ScanOutcome): Long {
        val riskLabel = when (outcome.riskLevel) {
            "Low Risk" -> "LOW RISK"
            "Medium Risk" -> "MEDIUM RISK"
            else -> "HIGH RISK"
        }
        val record = ScanHistoryRecord(
            id = 0L,
            scanType = if (outcome.isApk) "APK" else "URL",
            title = outcome.title,
            subtitle = outcome.subtitle,
            riskLabel = riskLabel,
            isHighRisk = RiskMapper.isHighRisk(outcome.riskLevel),
            riskScore = outcome.riskScore,
            targetDisplay = outcome.target,
            createdAtMs = System.currentTimeMillis(),
            summary = outcome.summary,
            reason = outcome.reason
        )
        val newId = historyRepository.insert(record)
        Log.d("HISTORY", "Saved scan to history: ${record.scanType} ${record.title} id=$newId")
        return newId
    }

    suspend fun getHistoryRecord(id: Long): ScanHistoryRecord? = historyRepository.getById(id)

    private suspend fun pollUntilDone(
        analysisId: String,
        onProgress: suspend (Float, String) -> Unit,
        refetch: suspend () -> AnalysisStats?
    ): AnalysisStats {
        for (attempt in 0 until MAX_POLL_ATTEMPTS) {
            if (!currentCoroutineContext().isActive) throw CancellationException()
            delay(POLL_INTERVAL_MS)
            val progressValue =
                (0.35f + 0.58f * (attempt + 1) / MAX_POLL_ATTEMPTS.toFloat()).coerceAtMost(0.96f)
            onProgress(progressValue, "Analyzing results...")
            val reportOrNull = api.getAnalysisOrNull(analysisId)
            val report = reportOrNull ?: continue
            val reportData = report.reportData ?: continue
            val attrs = reportData.attributes ?: continue
            val status = attrs.status
            if (status != "completed") continue
            val inline: AnalysisStats? = attrs.stats
            if (inline != null && RiskMapper.totalEngines(inline) > 0) return inline
            val refetched = refetch()
            if (refetched != null && RiskMapper.totalEngines(refetched) > 0) return refetched
        }
        throw IllegalStateException("Analysis taking longer than expected. Please try again later.")
    }

    private fun buildOutcomeUrl(url: String, stats: AnalysisStats): ScanOutcome {
        val score = RiskMapper.riskScoreFromStats(stats)
        val level = RiskMapper.riskLevelFromStats(stats)
        val title = if (url.length > 40) url.take(37) + "..." else url
        return ScanOutcome(
            riskScore = score,
            riskLevel = level,
            summary = RiskMapper.summaryFromStats(stats, false),
            reason = RiskMapper.reasonFromStats(stats),
            target = url,
            isApk = false,
            title = title,
            subtitle = "URL SCAN",
            hashShort = null,
            isFallback = false
        )
    }

    private fun buildOutcomeApk(
        fileName: String,
        hashShort: String,
        stats: AnalysisStats
    ): ScanOutcome {
        val score = RiskMapper.riskScoreFromStats(stats)
        val level = RiskMapper.riskLevelFromStats(stats)
        val title = if (fileName.length > 40) fileName.take(37) + "..." else fileName
        return ScanOutcome(
            riskScore = score,
            riskLevel = level,
            summary = RiskMapper.summaryFromStats(stats, true),
            reason = RiskMapper.reasonFromStats(stats),
            target = fileName,
            isApk = true,
            title = title,
            subtitle = "HASH: $hashShort",
            hashShort = hashShort,
            isFallback = false
        )
    }

    private fun buildFallbackUrl(normalizedUrl: String): ScanOutcome {
        val hintScore = StaticAnalysis.scoreUrl(normalizedUrl)
        val title = if (normalizedUrl.length > 40) normalizedUrl.take(37) + "..." else normalizedUrl
        val summary =
            "VirusTotal could not complete this scan (offline, missing API key, rate limit, or network issue). " +
                    "Local rule-based hint score: $hintScore/100. " +
                    "This fallback shows Medium risk at 5% so you still get a clear result for your demo."
        return ScanOutcome(
            riskScore = 5,
            riskLevel = "Medium Risk",
            summary = summary,
            reason = "Fallback result due to API limitation",
            target = normalizedUrl,
            isApk = false,
            title = title,
            subtitle = "URL SCAN",
            hashShort = null,
            isFallback = true
        )
    }

    private fun buildFallbackApk(uri: Uri, fileName: String): ScanOutcome {
        val sizeBytes = resolveFileSizeBytes(uri)
        val hintScore = StaticAnalysis.scoreApk(fileName, sizeBytes)
        val title = if (fileName.length > 40) fileName.take(37) + "..." else fileName
        val summary =
            "VirusTotal could not complete this scan (offline, missing API key, rate limit, or network issue). " +
                    "Local rule-based hint score: $hintScore/100. " +
                    "This fallback shows Medium risk at 5% so you still get a clear result for your demo."
        return ScanOutcome(
            riskScore = 5,
            riskLevel = "Medium Risk",
            summary = summary,
            reason = "Fallback result due to API limitation",
            target = fileName,
            isApk = true,
            title = title,
            subtitle = "HASH: N/A",
            hashShort = null,
            isFallback = true
        )
    }

    private fun resolveFileSizeBytes(uri: Uri): Long {
        try {
            val projection = arrayOf(OpenableColumns.SIZE)
            appContext.contentResolver.query(uri, projection, null, null, null)?.use { c ->
                if (c.moveToFirst()) {
                    val idx = c.getColumnIndex(OpenableColumns.SIZE)
                    if (idx >= 0) {
                        val size = c.getLong(idx)
                        if (size > 0L) return@resolveFileSizeBytes size
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FILE_SIZE", "Metadata error: ${e.message}")
        }

        return try {
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                input.available().toLong()
            } ?: -1L
        } catch (e: Exception) {
            Log.e("FILE_SIZE", "Stream error: ${e.message}")
            -1L
        }
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) {
            return trimmed
        }
        return "https://$trimmed"
    }

    private fun resolveDisplayName(uri: Uri): String? {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        appContext.contentResolver.query(uri, projection, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) return c.getString(idx)
            }
        }
        return uri.lastPathSegment
    }

    private fun sha256Hex(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(data)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
