package com.example.finalapkurl.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface VirusTotalApi {

    @GET("urls/{url_id}")
    suspend fun getUrlReport(@Path("url_id") urlId: String): Response<UrlReportResponse>

    @FormUrlEncoded
    @POST("urls")
    suspend fun submitUrl(@Field("url") url: String): SubmitResponse

    @GET("analyses/{id}")
    suspend fun getAnalysis(@Path("id") id: String): AnalysisReportResponse

    @GET("files/{hash}")
    suspend fun getFileReport(@Path("hash") hash: String): Response<FileReportResponse>

    @Multipart
    @POST("files")
    suspend fun uploadFile(@Part file: MultipartBody.Part): SubmitResponse
}
