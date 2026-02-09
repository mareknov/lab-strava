package com.lab.strava.domain.activity.dto

import com.lab.strava.domain.activity.model.Activity
import com.lab.strava.domain.activity.model.ActivityType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class ActivityResponse(
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
) {
  companion object {
    fun fromDomain(activity: Activity): ActivityResponse =
      ActivityResponse(
        id = activity.id,
        name = activity.name,
        type = activity.type,
        startDate = activity.startDate,
        startDateLocal = activity.startDateLocal,
        timezone = activity.timezone,
        distance = activity.distance,
        elapsedTime = activity.elapsedTime,
        movingTime = activity.movingTime,
        description = activity.description,
        totalElevationGain = activity.totalElevationGain,
        elevHigh = activity.elevHigh,
        elevLow = activity.elevLow,
        averageSpeed = activity.averageSpeed,
        maxSpeed = activity.maxSpeed,
        averageHeartrate = activity.averageHeartrate,
        maxHeartrate = activity.maxHeartrate,
        hasHeartrate = activity.hasHeartrate,
        averageCadence = activity.averageCadence,
        averageWatts = activity.averageWatts,
        maxWatts = activity.maxWatts,
        kilojoules = activity.kilojoules,
        calories = activity.calories,
        createdAt = activity.createdAt,
        updatedAt = activity.updatedAt,
      )
  }
}
