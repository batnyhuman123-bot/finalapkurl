package com.example.finalapkurl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableIntStateOf
import com.example.finalapkurl.ui.theme.FinalapkurlTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { AppContainer(this) }
    private val intentKeyState = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            intentKeyState.intValue = savedInstanceState.getInt(STATE_INTENT_KEY, 0)
        }
        Log.d(
            "INTENT_FLOW",
            "onCreate intentKey=${intentKeyState.intValue} action=${intent?.action} data=${intent?.data} type=${intent?.type}"
        )
        handleIncomingIntent(intent, fromNewIntent = false)
        enableEdgeToEdge()
        setContent {
            FinalapkurlTheme {
                val intentKey = intentKeyState.intValue
                ApkUrlApp(container = container, intentKey = intentKey)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent, fromNewIntent = true)
    }

    /**
     * Keeps [intent] in sync for Compose; bumps [intentKeyState] on new intents so
     * [ApkUrlApp] re-runs deep-link handling when the activity is already running (singleTop).
     */
    private fun handleIncomingIntent(intent: Intent?, fromNewIntent: Boolean) {
        if (intent == null) return
        Log.d(
            "INTENT_FLOW",
            "handleIncomingIntent fromNewIntent=$fromNewIntent action=${intent.action} data=${intent.data} type=${intent.type}"
        )
        intent.urlStringForDeepLink()?.let { url ->
            Log.d("INTENT_URL", url)
            Log.d("INTENT_URL", "intent received (${if (fromNewIntent) "onNewIntent" else "onCreate"})")
        }
        if (fromNewIntent) {
            intentKeyState.intValue++
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_INTENT_KEY, intentKeyState.intValue)
    }

    companion object {
        private const val STATE_INTENT_KEY = "intent_key_seq"
    }
}

/** VIEW intent: primary [Intent.getData], else [Intent.EXTRA_TEXT] when it looks like http(s). */
private fun Intent.urlStringForDeepLink(): String? {
    if (action != Intent.ACTION_VIEW) return null
    data?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
    return getStringExtra(Intent.EXTRA_TEXT)?.trim()?.takeIf { text ->
        text.startsWith("http://", true) || text.startsWith("https://", true)
    }
}
