package com.example.finalapkurl.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Normalised risk tier for UI styling. Parsed from stored labels such as
 * "LOW RISK", "MEDIUM RISK", "High Risk", etc.
 */
enum class RiskTier {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN
}

fun riskTierFromLabel(label: String): RiskTier {
    val u = label.trim().uppercase()
    return when {
        u.contains("HIGH") -> RiskTier.HIGH
        u.contains("MEDIUM") -> RiskTier.MEDIUM
        u.contains("LOW") -> RiskTier.LOW
        else -> RiskTier.UNKNOWN
    }
}

object RiskUiColors {
    val lowAccent = Color(0xFF2E7D32)
    val mediumAccent = Color(0xFFF57C00)
    val highAccent = Color.Red
    val unknownAccent = Color.Gray

    val lowChipBackground = Color(0xFFE8F5E9)
    val mediumChipBackground = Color(0xFFFFF3E0)
    val highChipBackground = Color(0xFFFFEBEE)
    val unknownChipBackground = Color(0xFFF5F5F5)

    val resultScreenBackgroundTintLow = Color(0xFFE8F5E9)
    val resultScreenBackgroundTintMedium = Color(0xFFFFF3E0)
    val resultScreenBackgroundTintHigh = Color(0xFFFFEBEE)

    val resultScoreBoxLow = Color(0xFFC8E6C9)
    val resultScoreBoxMedium = Color(0xFFFFE0B2)
    val resultScoreBoxHigh = Color(0xFFFFCDD2)
}

fun riskAccentColor(tier: RiskTier): Color = when (tier) {
    RiskTier.LOW -> RiskUiColors.lowAccent
    RiskTier.MEDIUM -> RiskUiColors.mediumAccent
    RiskTier.HIGH -> RiskUiColors.highAccent
    RiskTier.UNKNOWN -> RiskUiColors.unknownAccent
}

/**
 * Primary accent color for a risk label string (chips, reason line, etc.).
 */
fun getRiskColor(risk: String): Color = riskAccentColor(riskTierFromLabel(risk))

fun riskChipBackgroundColor(tier: RiskTier): Color = when (tier) {
    RiskTier.LOW -> RiskUiColors.lowChipBackground
    RiskTier.MEDIUM -> RiskUiColors.mediumChipBackground
    RiskTier.HIGH -> RiskUiColors.highChipBackground
    RiskTier.UNKNOWN -> RiskUiColors.unknownChipBackground
}

fun riskChipBackgroundColorForLabel(label: String): Color =
    riskChipBackgroundColor(riskTierFromLabel(label))

fun riskResultScreenBackgroundTint(tier: RiskTier): Color = when (tier) {
    RiskTier.LOW -> RiskUiColors.resultScreenBackgroundTintLow
    RiskTier.MEDIUM -> RiskUiColors.resultScreenBackgroundTintMedium
    RiskTier.HIGH -> RiskUiColors.resultScreenBackgroundTintHigh
    RiskTier.UNKNOWN -> RiskUiColors.unknownChipBackground
}

fun riskResultScoreBoxColor(tier: RiskTier): Color = when (tier) {
    RiskTier.LOW -> RiskUiColors.resultScoreBoxLow
    RiskTier.MEDIUM -> RiskUiColors.resultScoreBoxMedium
    RiskTier.HIGH -> RiskUiColors.resultScoreBoxHigh
    RiskTier.UNKNOWN -> RiskUiColors.unknownChipBackground
}
