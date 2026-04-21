package com.example.finalapkurl

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finalapkurl.navigation.Routes
import com.example.finalapkurl.presentation.HistoryViewModel
import com.example.finalapkurl.presentation.HomeViewModel
import com.example.finalapkurl.presentation.ScanViewModel
import com.example.finalapkurl.ui.screens.ApkScannerScreen
import com.example.finalapkurl.ui.screens.HistoryScreen
import com.example.finalapkurl.ui.screens.HomeScreen
import com.example.finalapkurl.ui.screens.PrivacyPolicyScreen
import com.example.finalapkurl.ui.screens.ResultScreen
import com.example.finalapkurl.ui.screens.ScanningScreen
import com.example.finalapkurl.ui.screens.SettingsScreen
import com.example.finalapkurl.ui.screens.UrlScannerScreen
import com.example.finalapkurl.ui.util.getFileName
import kotlinx.coroutines.launch

@Composable
fun ApkUrlApp(
    container: AppContainer,
    intentKey: Int
) {
    val activity = LocalActivity.current as ComponentActivity
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val scanViewModel: ScanViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = container.scanViewModelFactory
    )
    val homeViewModel: HomeViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = container.homeViewModelFactory
    )
    val historyViewModel: HistoryViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = container.historyViewModelFactory
    )

    val homeLatest by homeViewModel.latest.collectAsState()
    val totalScans by homeViewModel.totalScans.collectAsState()
    val highRisk by homeViewModel.highRiskCount.collectAsState()

    val historyItems by historyViewModel.items.collectAsState()

    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route
    val showBottomBar = currentRoute in setOf(
        Routes.HOME,
        Routes.HISTORY,
        Routes.SETTINGS
    )

    // Deduplicate by intent snapshot (intentKey + data + action), not intentKey alone — avoids
    // skipping a cold-start VIEW after process restore when intentKey and lastHandled both equal 0.
    var lastProcessedIntentToken by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scanViewModel.navigateToResult.collect { scanId ->
            navController.navigate(Routes.resultRoute(scanId)) {
                popUpTo(Routes.SCANNING) { inclusive = true }
            }
        }
    }

    LaunchedEffect(intentKey) {
        val intent = activity.intent
        val token = "${intentKey}|${intent.dataString}|${intent.action}|${intent.type}"
        if (token == lastProcessedIntentToken) return@LaunchedEffect
        Log.d(
            "INTENT_FLOW",
            "Processing intent intentKey=$intentKey action=${intent.action} data=${intent.data} type=${intent.type}"
        )
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = resolveViewIntentUri(intent) ?: run {
                    Log.d("INTENT_FLOW", "VIEW intent has no resolvable URI; skipping")
                    return@LaunchedEffect
                }
                when {
                    uri.scheme.equals("http", true) || uri.scheme.equals("https", true) -> {
                        if (!isValidHttpUriForScan(uri)) {
                            Log.d("INTENT_FLOW", "VIEW URL invalid (missing host or scheme); skipping")
                            return@LaunchedEffect
                        }
                        val url = uri.toString()
                        Log.d("INTENT_URL", url)
                        Log.d("INTENT_URL", "scan triggered (navigate + prepareUrlScan)")
                        scanViewModel.resetSession()
                        scanViewModel.prepareUrlScan(url)
                        navController.navigate(Routes.SCANNING) {
                            launchSingleTop = true
                        }
                        lastProcessedIntentToken = token
                        Log.d("INTENT_FLOW", "Navigated to scan URL: $uri")
                    }

                    uri.scheme.equals("content", true) || uri.scheme.equals("file", true) -> {
                        scanViewModel.resetSession()
                        scanViewModel.prepareApkScan(uri)
                        navController.navigate(Routes.SCANNING) {
                            launchSingleTop = true
                        }
                        lastProcessedIntentToken = token
                        Log.d("INTENT_FLOW", "Navigated to scan APK: $uri")
                    }

                    else -> {
                        Log.d("INTENT_FLOW", "VIEW scheme not handled: ${uri.scheme}")
                        return@LaunchedEffect
                    }
                }
            }

            Intent.ACTION_SEND -> {
                val mime = intent.type.orEmpty()
                if (mime.startsWith("text/")) {
                    val text = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
                    val url = extractHttpUrlFromText(text)
                    if (url == null || !isValidHttpUrlString(url)) {
                        Log.d("INTENT_FLOW", "SEND text/plain has no valid http(s) URL; skipping")
                        return@LaunchedEffect
                    }
                    Log.d("INTENT_URL", url)
                    scanViewModel.resetSession()
                    scanViewModel.prepareUrlScan(url)
                    navController.navigate(Routes.SCANNING) {
                        launchSingleTop = true
                    }
                    lastProcessedIntentToken = token
                    Log.d("INTENT_FLOW", "Navigated to scan URL from SEND: $url")
                    return@LaunchedEffect
                }
                if (mime.contains("application/vnd.android.package-archive") ||
                    mime.contains("octet-stream")
                ) {
                    val uri = if (android.os.Build.VERSION.SDK_INT >= 33) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    } ?: run {
                        Log.d("INTENT_FLOW", "SEND missing stream URI")
                        return@LaunchedEffect
                    }
                    scanViewModel.resetSession()
                    scanViewModel.prepareApkScan(uri)
                    navController.navigate(Routes.SCANNING) {
                        launchSingleTop = true
                    }
                    lastProcessedIntentToken = token
                    Log.d("INTENT_FLOW", "Navigated to scan SEND APK: $uri")
                }
            }
        }
    }

    Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Text("⌂", color = Color.Black) },
                            label = { Text("Home", color = Color.Black) },
                            selected = currentRoute == Routes.HOME,
                            onClick = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Text("☰", color = Color.Black) },
                            label = { Text("History", color = Color.Black) },
                            selected = currentRoute == Routes.HISTORY,
                            onClick = {
                                navController.navigate(Routes.HISTORY) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Text("⚙", color = Color.Black) },
                            label = { Text("Settings", color = Color.Black) },
                            selected = currentRoute == Routes.SETTINGS,
                            onClick = {
                                navController.navigate(Routes.SETTINGS) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(padding)
            ) {
                composable(Routes.HOME) {
                    val lastActivityText = if (homeLatest.isEmpty()) {
                        "none yet"
                    } else {
                        formatLastActivityLabel(homeLatest.first().createdAtMs)
                    }
                    HomeScreen(
                        totalScans = totalScans.toString(),
                        highRisks = highRisk.toString(),
                        lastActivityLabel = lastActivityText,
                        recent = homeLatest,
                        onScanUrl = { navController.navigate(Routes.URL_SCAN) },
                        onScanApk = { navController.navigate(Routes.APK_SCAN) },
                        onRecentItemClick = { entity ->
                            scanViewModel.openHistoryResult(entity)
                            navController.navigate(Routes.resultRoute(entity.id))
                        }
                    )
                }
                composable(Routes.URL_SCAN) {
                    UrlScannerScreen(
                        onBack = { navController.popBackStack() },
                        onScanClick = { url ->
                            scanViewModel.resetSession()
                            scanViewModel.prepareUrlScan(url)
                            navController.navigate(Routes.SCANNING)
                        }
                    )
                }
                composable(Routes.APK_SCAN) {
                    val context = LocalContext.current
                    var selectedUri by remember { mutableStateOf<Uri?>(null) }
                    var fileDisplayName by remember { mutableStateOf("Choose APK File") }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri ->
                        uri?.let {
                            selectedUri = it
                            fileDisplayName = getFileName(context, it)
                        } ?: run {
                            selectedUri = null
                            fileDisplayName = "Choose APK File"
                        }
                    }
                    ApkScannerScreen(
                        onBack = { navController.popBackStack() },
                        onSelectApk = {
                            launcher.launch("application/vnd.android.package-archive")
                        },
                        onScanClick = {
                            val u = selectedUri ?: return@ApkScannerScreen
                            scanViewModel.resetSession()
                            scanViewModel.prepareApkScan(u)
                            navController.navigate(Routes.SCANNING)
                        },
                        hasFileSelected = selectedUri != null,
                        fileDisplayName = fileDisplayName
                    )
                }
                composable(Routes.SCANNING) {
                    val progress by scanViewModel.progress.collectAsState()
                    val status by scanViewModel.statusMessage.collectAsState()
                    val error by scanViewModel.error.collectAsState()
                    val scanTitle by scanViewModel.scanKindLabel.collectAsState()
                    val scanSession by scanViewModel.scanSession.collectAsState()

                    LaunchedEffect(scanSession) {
                        if (scanSession == 0) return@LaunchedEffect
                        scanViewModel.startScan()
                    }

                    LaunchedEffect(error) {
                        val msg = error ?: return@LaunchedEffect
                        scope.launch {
                            snackbarHostState.showSnackbar(msg)
                            scanViewModel.clearError()
                            navController.popBackStack()
                        }
                    }

                    ScanningScreen(
                        title = scanTitle,
                        progress = progress,
                        statusMessage = status,
                        onBack = {
                            scanViewModel.resetSession()
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = Routes.RESULT,
                    arguments = listOf(
                        navArgument("scanId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val scanId = backStackEntry.arguments?.getLong("scanId") ?: 0L
                    val outcome by scanViewModel.result.collectAsState()
                    LaunchedEffect(scanId) {
                        if (scanId <= 0L) {
                            navController.popBackStack()
                            return@LaunchedEffect
                        }
                        val ok = scanViewModel.resolveHistoryIntoResult(scanId)
                        if (!ok && scanViewModel.result.value == null) {
                            navController.popBackStack()
                        }
                    }
                    val o = outcome
                    if (o != null) {
                        ResultScreen(
                            riskLevel = o.riskLevel,
                            riskScore = o.riskScore,
                            url = o.target,
                            summary = o.summary,
                            reason = o.reason,
                            onDone = {
                                scanViewModel.consumeResult()
                                scanViewModel.resetSession()
                                navController.navigate(Routes.HOME) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
                composable(Routes.HISTORY) {
                    HistoryScreen(
                        items = historyItems,
                        onConfirmClearAll = { historyViewModel.clearAll() },
                        onItemClick = { entity ->
                            scanViewModel.openHistoryResult(entity)
                            navController.navigate(Routes.resultRoute(entity.id))
                        },
                        onDeleteSelected = { ids -> historyViewModel.deleteByIds(ids) }
                    )
                }
                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        onPrivacyClick = { navController.navigate(Routes.PRIVACY) }
                    )
                }
                composable(Routes.PRIVACY) {
                    PrivacyPolicyScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
}

private fun resolveViewIntentUri(intent: Intent): Uri? {
    intent.data?.let { return it }
    intent.clipData?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.uri?.let { return it }
    intent.getStringExtra(Intent.EXTRA_TEXT)?.trim()?.let { text ->
        extractHttpUrlFromText(text)?.let { url ->
            return try {
                Uri.parse(url)
            } catch (_: Exception) {
                null
            }
        }
    }
    return null
}

/** Requires a normal http(s) URL with a non-blank host (avoids malformed / empty scans). */
private fun isValidHttpUriForScan(uri: Uri): Boolean {
    if (!uri.scheme.equals("http", true) && !uri.scheme.equals("https", true)) return false
    return try {
        !uri.host.isNullOrBlank()
    } catch (_: Exception) {
        false
    }
}

private fun isValidHttpUrlString(url: String): Boolean =
    try {
        isValidHttpUriForScan(Uri.parse(url))
    } catch (_: Exception) {
        false
    }

/** Picks first http(s) URL from shared text (e.g. WhatsApp message body). */
private fun extractHttpUrlFromText(text: String): String? {
    if (text.isBlank()) return null
    val trimmed = text.trim()
    val word = trimmed.split(Regex("\\s+")).firstOrNull { part ->
        part.startsWith("http://", ignoreCase = true) || part.startsWith("https://", ignoreCase = true)
    }
    if (word != null) return word.trim().trimEnd(',', '.', ';', ')')
    return Regex("https?://[^\\s<>\"()]+").find(text)?.value
}

private fun formatLastActivityLabel(createdAtMs: Long): String {
    val diff = System.currentTimeMillis() - createdAtMs
    val mins = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff)
    if (mins < 1) return "just now"
    if (mins < 60) return "$mins mins ago"
    val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diff)
    if (hours < 24) return if (hours == 1L) "1 hour ago" else "$hours hours ago"
    val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)
    if (days == 1L) return "Yesterday"
    return "$days days ago"
}
