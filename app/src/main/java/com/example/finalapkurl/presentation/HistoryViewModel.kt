package com.example.finalapkurl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalapkurl.data.local.HistoryRepository
import com.example.finalapkurl.data.local.ScanHistoryRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val items: StateFlow<List<ScanHistoryRecord>> = historyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    /**
     * Removes the given scan rows from local history ([HistoryRepository], DataStore-backed JSON).
     * History list and Home totals (total scans, high-risk count) refresh via the same repository flows.
     */
    fun deleteByIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            historyRepository.deleteByIds(ids)
        }
    }

    companion object {
        fun factory(historyRepository: HistoryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(historyRepository) as T
                }
            }
    }
}
