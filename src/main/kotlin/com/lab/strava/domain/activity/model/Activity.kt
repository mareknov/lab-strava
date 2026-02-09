package com.lab.strava.domain.activity.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Activity(
  val id: UUID,
  val name: String,
  val type: ActivityType,
  val startDate: Instant,
  val startDateLocal: Instant,
  val timezone: String,
  val distance: BigDecimal,
  val elapsedTime: Int,
  val movingTime: Int,
  val description: String?,
  val totalElevationGain: BigDecimal?,
  val elevHigh: BigDecimal?,
  val elevLow: BigDecimal?,
  val averageSpeed: BigDecimal?,
  val maxSpeed: BigDecimal?,
  val averageHeartrate: Int?,
  val maxHeartrate: Int?,
  val hasHeartrate: Boolean,
  val averageCadence: Int?,
  val averageWatts: Int?,
  val maxWatts: Int?,
  val kilojoules: BigDecimal?,
  val calories: Int?,
  val createdAt: Instant,
  val updatedAt: Instant,
)
