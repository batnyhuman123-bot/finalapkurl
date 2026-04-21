package com.example.finalapkurl.data.local

import com.example.finalapkurl.data.ScanOutcome

fun ScanHistoryRecord.toScanOutcome(): ScanOutcome {
    val level = when {
        riskLabel.contains("LOW", ignoreCase = true) -> "Low Risk"
        riskLabel.contains("MEDIUM", ignoreCase = true) -> "Medium Risk"
        else -> "High Risk"
    }
    val summaryText = summary?.takeIf { it.isNotBlank() }
        ?: "Historical scan — reopen to review stored details."
    val reasonText = reason?.takeIf { it.isNotBlank() }
        ?: when {
            riskLabel.contains("LOW", ignoreCase = true) -> "Verified Safe"
            riskLabel.contains("MEDIUM", ignoreCase = true) -> "Suspicious Activity"
            else -> "Malicious Detected"
        }
    return ScanOutcome(
        riskScore = riskScore,
        riskLevel = level,
        summary = summaryText,
        reason = reasonText,
        target = targetDisplay,
        isApk = scanType == "APK",
        title = title,
        subtitle = subtitle,
        hashShort = null,
        isFallback = false
    )
}
