package com.example.finalapkurl.domain

import com.example.finalapkurl.data.remote.AnalysisStats
import kotlin.math.roundToInt

/**
 * VirusTotal [AnalysisStats] mapping.
 *
 * Risk score: (malicious + suspicious) / totalEngines * 100, where totalEngines is the sum of
 * all VT engine categories for that report.
 *
 * Risk level from score: Low (0), Medium (1–10), High (>10).
 */
object RiskMapper {

    fun totalEngines(stats: AnalysisStats): Int = stats.totalEngines()

    /**
     * Risk score: round((M + S) / totalEngines * 100), capped 0–100. If totalEngines == 0, returns 0.
     */
    fun riskScoreFromStats(stats: AnalysisStats): Int {
        val m = stats.malicious
        val s = stats.suspicious
        val t = totalEngines(stats)
        if (t <= 0) return 0
        return ((m + s) * 100f / t).roundToInt().coerceIn(0, 100)
    }

    fun riskLevelFromStats(stats: AnalysisStats): String {
        val score = riskScoreFromStats(stats)
        return when {
            score == 0 -> "Low Risk"
            score in 1..10 -> "Medium Risk"
            else -> "High Risk"
        }
    }

    fun reasonFromLevel(level: String): String = when (level) {
        "Low Risk" -> "Verified Safe"
        "Medium Risk" -> "Suspicious Activity"
        else -> "Malicious Detected"
    }

    fun reasonFromStats(stats: AnalysisStats): String =
        reasonFromLevel(riskLevelFromStats(stats))

    /**
     * Detection ratio string e.g. "5/70" where numerator = M+S, denominator = totalEngines.
     */
    fun detectionRatioString(stats: AnalysisStats): String {
        val m = stats.malicious
        val s = stats.suspicious
        val t = totalEngines(stats)
        val detections = m + s
        return "$detections/$t"
    }

    fun summaryFromStats(stats: AnalysisStats, isApk: Boolean): String {
        val ratioLine = "Detection ratio: ${detectionRatioString(stats)}."
        val body = summaryBodyFromLevel(riskLevelFromStats(stats), isApk)
        return "$ratioLine\n\n$body"
    }

    private fun summaryBodyFromLevel(level: String, isApk: Boolean): String = when (level) {
        "Low Risk" ->
            if (isApk) {
                "Analysis complete. This APK appears to be safe based on our current security database and heuristic checks."
            } else {
                "Analysis complete. This URL appears to be safe based on our current security database and heuristic checks."
            }

        "Medium Risk" ->
            if (isApk) {
                "This APK shows some suspicious indicators. Proceed with caution before installing."
            } else {
                "This URL shows some suspicious indicators. Proceed with caution and avoid entering sensitive information."
            }

        else ->
            if (isApk) {
                "Warning! This APK is flagged as potentially malicious. It may contain harmful content."
            } else {
                "Warning! This URL is flagged as potentially malicious. It may contain phishing or harmful content."
            }
    }

    fun isHighRisk(level: String): Boolean = level == "High Risk"
}
