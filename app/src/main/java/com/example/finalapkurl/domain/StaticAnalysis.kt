package com.example.finalapkurl.domain

/**
 * Rule-based scoring when VirusTotal is unavailable. Score is 0–100.
 * Risk level: &lt;20 → Low, 20–50 → Medium, &gt;50 → High (mapped to ResultScreen strings).
 */
object StaticAnalysis {

    private const val MB = 1024L * 1024L

    fun scoreUrl(normalizedUrl: String): Int {
        var score = 0
        val lower = normalizedUrl.lowercase()
        if (lower.startsWith("http://")) score += 15
        if (normalizedUrl.length > 50) score += 10
        if (lower.contains("free")) score += 12
        if (lower.contains("crack")) score += 25
        if (lower.contains("download")) score += 8
        if (lower.contains("apk")) score += 10
        return score.coerceIn(0, 100)
    }

    /**
     * @param fileSizeBytes use -1 if unknown (skips size-based rules)
     */
    fun scoreApk(fileName: String, fileSizeBytes: Long): Int {
        var score = 0
        val lower = fileName.lowercase()
        if (lower.contains("mod")) score += 20
        if (lower.contains("crack")) score += 25
        if (fileSizeBytes >= 0) {
            if (fileSizeBytes in 1 until MB) score += 15
            if (fileSizeBytes in 1 until (512 * 1024)) score += 10
        }
        return score.coerceIn(0, 100)
    }

    fun riskLevelFromStaticScore(score: Int): String = when {
        score < 20 -> "Low Risk"
        score in 20..50 -> "Medium Risk"
        else -> "High Risk"
    }
}
