package com.example.finalapkurl

import android.content.Context
import com.example.finalapkurl.data.ScanRepository
import com.example.finalapkurl.data.local.HistoryRepository
import com.example.finalapkurl.data.remote.VirusTotalModule
import com.example.finalapkurl.presentation.HistoryViewModel
import com.example.finalapkurl.presentation.HomeViewModel
import com.example.finalapkurl.presentation.ScanViewModel

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val historyRepository: HistoryRepository = HistoryRepository(appContext)

    val scanRepository: ScanRepository = ScanRepository(
        appContext,
        VirusTotalModule.createApi(),
        historyRepository,
        BuildConfig.VIRUSTOTAL_API_KEY
    )

    val scanViewModelFactory = ScanViewModel.factory(scanRepository)
    val homeViewModelFactory = HomeViewModel.factory(historyRepository)
    val historyViewModelFactory = HistoryViewModel.factory(historyRepository)
}
