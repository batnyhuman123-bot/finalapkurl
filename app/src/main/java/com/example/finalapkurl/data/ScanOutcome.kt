package com.example.finalapkurl.data

data class ScanOutcome(
    val riskScore: Int,
    val riskLevel: String,
    val summary: String,
    val reason: String,
    val target: String,
    val isApk: Boolean,
    val title: String,
    val subtitle: String,
    val hashShort: String?,
    val isFallback: Boolean = false
)
