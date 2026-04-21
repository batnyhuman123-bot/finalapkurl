package com.example.finalapkurl.ui.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Resolves a human-readable file name for a content [Uri] (e.g. picked APK).
 */
fun getFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )
    val fromMeta = cursor?.use { c ->
        if (!c.moveToFirst()) return@use null
        val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx < 0) return@use null
        c.getString(idx)?.takeIf { it.isNotBlank() }
    }
    if (!fromMeta.isNullOrBlank()) return fromMeta

    val segment = uri.lastPathSegment
    if (!segment.isNullOrBlank()) {
        val decoded = Uri.decode(segment).substringAfterLast('/')
        if (decoded.isNotBlank()) return decoded
    }
    return "Unknown APK"
}
