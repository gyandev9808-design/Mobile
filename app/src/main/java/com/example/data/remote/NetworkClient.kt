package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
  const val DEFAULT_BASE_URL = "https://ais-pre-neevnxgqtzrd7gvloq73u5-782596577017.asia-southeast1.run.app/"
  const val DOMAIN_ALIAS = "delegatex.ai.studio"

  private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .writeTimeout(5, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BASIC
    })
    .build()

  private var currentBaseUrl = DEFAULT_BASE_URL
  private var apiServiceInstance: DelegateXApiService? = null

  fun getApiService(baseUrl: String = currentBaseUrl): DelegateXApiService {
    val sanitizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    if (apiServiceInstance == null || sanitizedUrl != currentBaseUrl) {
      currentBaseUrl = sanitizedUrl
      apiServiceInstance = Retrofit.Builder()
        .baseUrl(sanitizedUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(DelegateXApiService::class.java)
    }
    return apiServiceInstance!!
  }

  fun getCurrentBaseUrl(): String = currentBaseUrl
}
