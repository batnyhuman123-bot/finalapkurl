package com.example.finalapkurl.data.local

data class ScanHistoryRecord(
    val id: Long,
    val scanType: String,
    val title: String,
    val subtitle: String,
    val riskLabel: String,
    val isHighRisk: Boolean,
    val riskScore: Int,
    val targetDisplay: String,
    val createdAtMs: Long,
    val summary: String? = null,
    val reason: String? = null
)
