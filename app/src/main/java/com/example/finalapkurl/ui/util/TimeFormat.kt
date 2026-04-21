package com.example.finalapkurl.ui.util

import java.util.concurrent.TimeUnit

fun formatRelativeTime(createdAtMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - createdAtMs
    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
    if (mins < 1) return "Just now"
    if (mins < 60) return "$mins mins ago"
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    if (hours < 24) return if (hours == 1L) "1 hour ago" else "$hours hours ago"
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    if (days == 1L) return "Yesterday"
    if (days < 7) return "$days days ago"
    return java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(createdAtMs)
}
