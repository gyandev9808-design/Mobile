package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chairs")
data class ChairEntity(
  @PrimaryKey val id: String,
  val fullName: String,
  val email: String,
  val institution: String,
  val committee: String,
  val position: String,
  val securityPin: String,
  val openingStatement: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val syncedWithServer: Boolean = true
)

@Entity(tableName = "delegates")
data class DelegateEntity(
  @PrimaryKey val id: String,
  val fullName: String,
  val email: String,
  val institution: String,
  val committee: String,
  val country: String,
  val countryCode: String,
  val experienceLevel: String, // Novice, Intermediate, Veteran
  val positionPaperTopic: String = "",
  val rollCallStatus: String = "PRESENT_AND_VOTING", // PRESENT_AND_VOTING, PRESENT, ABSENT, UNRECORDED
  val registeredAt: Long = System.currentTimeMillis(),
  val syncedWithServer: Boolean = true
)

@Entity(tableName = "committees")
data class CommitteeEntity(
  @PrimaryKey val id: String, // e.g. "UNSC", "UNHRC", "DISEC", "UNEP", "WHO", "CRISIS"
  val name: String,
  val topic: String,
  val room: String,
  val sessionStatus: String = "IN_SESSION", // IN_SESSION, RECESS, ADJOURNED
  val currentGslSpeaker: String? = null,
  val gslSpeakerTimeSeconds: Int = 90,
  val caucusTopic: String? = null,
  val caucusTimeRemainingSeconds: Int = 0,
  val isTimerRunning: Boolean = false
)

@Entity(tableName = "session_actions")
data class SessionActionEntity(
  @PrimaryKey val id: String,
  val committeeId: String,
  val actorName: String,
  val actorRole: String, // CHAIR, DELEGATE
  val actionType: String, // ROLL_CALL, GSL_SPEECH, MOTION, POINT, RESOLUTION_VOTE
  val details: String,
  val timestamp: Long = System.currentTimeMillis()
)
