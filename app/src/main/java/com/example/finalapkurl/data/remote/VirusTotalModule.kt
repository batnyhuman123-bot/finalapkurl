package com.example.finalapkurl.data.remote

import android.util.Log
import com.example.finalapkurl.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object VirusTotalModule {

    private const val BASE_URL = "https://www.virustotal.com/api/v3/"

    fun createApi(): VirusTotalApi {
        val key = BuildConfig.VIRUSTOTAL_API_KEY
        if (BuildConfig.DEBUG) {
            Log.d("API_KEY_CHECK", BuildConfig.VIRUSTOTAL_API_KEY)
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("x-apikey", key)
                    .build()
                chain.proceed(req)
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VirusTotalApi::class.java)
    }
}
