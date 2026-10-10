package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerStatusResponse(
  @Json(name = "status") val status: String = "online",
  @Json(name = "platform") val platform: String = "DelegateX AI Studio Conference Hub",
  @Json(name = "server") val server: String = "delegatex.ai.studio",
  @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class ChairApiDto(
  @Json(name = "id") val id: String,
  @Json(name = "fullName") val fullName: String,
  @Json(name = "email") val email: String,
  @Json(name = "institution") val institution: String,
  @Json(name = "committee") val committee: String,
  @Json(name = "position") val position: String,
  @Json(name = "openingStatement") val openingStatement: String = "",
  @Json(name = "registeredAt") val registeredAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class DelegateApiDto(
  @Json(name = "id") val id: String,
  @Json(name = "fullName") val fullName: String,
  @Json(name = "email") val email: String,
  @Json(name = "institution") val institution: String,
  @Json(name = "committee") val committee: String,
  @Json(name = "country") val country: String,
  @Json(name = "countryCode") val countryCode: String,
  @Json(name = "experienceLevel") val experienceLevel: String,
  @Json(name = "positionPaperTopic") val positionPaperTopic: String = "",
  @Json(name = "rollCallStatus") val rollCallStatus: String = "PRESENT_AND_VOTING"
)

@JsonClass(generateAdapter = true)
data class RegistrationApiResponse(
  @Json(name = "success") val success: Boolean,
  @Json(name = "message") val message: String,
  @Json(name = "registeredId") val registeredId: String? = null,
  @Json(name = "serverSyncTime") val serverSyncTime: Long = System.currentTimeMillis()
)
