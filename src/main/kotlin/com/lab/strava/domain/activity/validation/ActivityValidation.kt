package com.lab.strava.domain.activity.validation

import com.lab.strava.domain.activity.dto.CreateActivityRequest
import java.math.BigDecimal
import java.time.ZoneId

object ActivityValidation {
  fun validateCreateRequest(request: CreateActivityRequest) {
    validateDistance(request.distance)
    validateElapsedTime(request.elapsedTime)
    validateMovingTime(request.movingTime, request.elapsedTime)
    validateElevation(request.elevHigh, request.elevLow)
    validateSpeed(request.maxSpeed, request.averageSpeed)
    validateHeartrate(request.averageHeartrate, request.maxHeartrate)
    validateTimezone(request.timezone)
  }

  private fun validateDistance(distance: BigDecimal?) {
    requireNotNull(distance) { "Distance is required" }
    require(distance >= BigDecimal.ZERO) { "Distance must be >= 0" }
  }

  private fun validateElapsedTime(elapsedTime: Int?) {
    requireNotNull(elapsedTime) { "Elapsed time is required" }
    require(elapsedTime > 0) { "Elapsed time must be > 0" }
  }

  private fun validateMovingTime(
    movingTime: Int?,
    elapsedTime: Int?,
  ) {
    requireNotNull(movingTime) { "Moving time is required" }
    require(movingTime > 0) { "Moving time must be > 0" }
    if (elapsedTime != null) {
      require(movingTime <= elapsedTime) { "Moving time must be <= elapsed time" }
    }
  }

  private fun validateElevation(
    elevHigh: BigDecimal?,
    elevLow: BigDecimal?,
  ) {
    if (elevHigh != null && elevLow != null) {
      require(elevHigh > elevLow) { "Elevation high must be > elevation low" }
    }
  }

  private fun validateSpeed(
    maxSpeed: BigDecimal?,
    averageSpeed: BigDecimal?,
  ) {
    if (maxSpeed != null && averageSpeed != null) {
      require(maxSpeed >= averageSpeed) { "Max speed must be >= average speed" }
    }
  }

  private fun validateHeartrate(
    averageHeartrate: Int?,
    maxHeartrate: Int?,
  ) {
    if (averageHeartrate != null) {
      require(averageHeartrate in 0..300) { "Average heartrate must be between 0 and 300 bpm" }
    }
    if (maxHeartrate != null) {
      require(maxHeartrate in 0..300) { "Max heartrate must be between 0 and 300 bpm" }
    }
  }

  private fun validateTimezone(timezone: String) {
    try {
      ZoneId.of(timezone)
    } catch (e: Exception) {
      throw IllegalArgumentException("Invalid timezone: $timezone")
    }
  }
}
