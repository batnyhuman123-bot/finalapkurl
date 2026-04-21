package com.example.finalapkurl.data.remote

import android.util.Log
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

suspend fun VirusTotalApi.getUrlReportStats(urlId: String): AnalysisStats? {
    val response: Response<UrlReportResponse> = getUrlReport(urlId)
    Log.d("VT_RESPONSE", "getUrlReport code=${response.code()} success=${response.isSuccessful}")
    if (!response.isSuccessful) return null
    return response.body()?.data?.attributes?.lastAnalysisStats
}

suspend fun VirusTotalApi.getFileReportStats(hash: String): AnalysisStats? {
    val response: Response<FileReportResponse> = getFileReport(hash)
    Log.d("VT_RESPONSE", "getFileReport code=${response.code()} success=${response.isSuccessful}")
    if (!response.isSuccessful) return null
    return response.body()?.data?.attributes?.lastAnalysisStats
}

suspend fun VirusTotalApi.getAnalysisOrNull(analysisId: String): AnalysisReportResponse? {
    return try {
        val r = getAnalysis(analysisId)
        Log.d("VT_RESPONSE", "getAnalysis ok")
        r
    } catch (e: CancellationException) {
        throw e
    } catch (ex: Exception) {
        Log.d("VT_RESPONSE", "getAnalysis failed: ${ex.message}")
        null
    }
}

fun apkMultipartPart(fileName: String, bytes: ByteArray): MultipartBody.Part {
    val body = bytes.toRequestBody("application/vnd.android.package-archive".toMediaType())
    return MultipartBody.Part.createFormData("file", fileName, body)
}
