package com.example.finalapkurl.data.remote

import com.google.gson.annotations.SerializedName

data class UrlReportResponse(
    val data: UrlReportData?
)

data class UrlReportData(
    val attributes: UrlReportAttributes?
)

data class UrlReportAttributes(
    @SerializedName("last_analysis_stats") val lastAnalysisStats: AnalysisStats?
)

data class FileReportResponse(
    val data: FileReportData?
)

data class FileReportData(
    val attributes: FileReportAttributes?
)

data class FileReportAttributes(
    @SerializedName("last_analysis_stats") val lastAnalysisStats: AnalysisStats?
)

data class AnalysisStats(
    val malicious: Int = 0,
    val suspicious: Int = 0,
    val undetected: Int = 0,
    val harmless: Int = 0,
    val timeout: Int = 0,
    val failure: Int = 0,
    @SerializedName("type-unsupported") val typeUnsupported: Int = 0,
    @SerializedName("confirmed-timeout") val confirmedTimeout: Int = 0
) {
    fun totalEngines(): Int =
        malicious + suspicious + undetected + harmless + timeout + failure + typeUnsupported + confirmedTimeout
}

data class SubmitResponse(
    @SerializedName("data") val submitData: SubmitData?
)

data class SubmitData(
    val id: String?,
    val type: String?
)

data class AnalysisReportResponse(
    @SerializedName("data") val reportData: AnalysisReportData?
)

data class AnalysisReportData(
    val attributes: AnalysisReportAttributes?
)

data class AnalysisReportAttributes(
    val status: String?,
    val stats: AnalysisStats?
)
