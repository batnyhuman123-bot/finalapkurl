package com.example.finalapkurl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalapkurl.data.local.HistoryRepository
import com.example.finalapkurl.data.local.ScanHistoryRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    historyRepository: HistoryRepository
) : ViewModel() {

    val latest: StateFlow<List<ScanHistoryRecord>> = historyRepository.observeLatest5()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalScans: StateFlow<Int> = historyRepository.observeTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val highRiskCount: StateFlow<Int> = historyRepository.observeHighRiskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    companion object {
        fun factory(historyRepository: HistoryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(historyRepository) as T
                }
            }
    }
}
