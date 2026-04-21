package com.example.finalapkurl.navigation

object Routes {
    const val HOME = "home"
    const val URL_SCAN = "url_scan"
    const val APK_SCAN = "apk_scan"
    const val SCANNING = "scanning"
    const val RESULT = "result/{scanId}"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val PRIVACY = "privacy"

    fun resultRoute(scanId: Long) = "result/$scanId"
}
