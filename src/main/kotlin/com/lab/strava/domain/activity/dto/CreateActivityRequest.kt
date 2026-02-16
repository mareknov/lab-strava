package com.lab.strava.domain.activity.dto

import com.lab.strava.domain.activity.model.ActivityType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant

data class CreateActivityRequest(
  @field:NotBlank(message = "Name is required")
  val name: String,

  @field:NotNull(message = "Type is required")
  val type: ActivityType?,

  @field:NotNull(message = "Start date is required")
  val startDate: Instant?,

  @field:NotNull(message = "Start date local is required")
  val startDateLocal: Instant?,

  @field:NotBlank(message = "Timezone is required")
  val timezone: String,

  @field:NotNull(message = "Distance is required")
  @field:DecimalMin(value = "0", message = "Distance must be >= 0")
  val distance: BigDecimal?,

  @field:NotNull(message = "Elapsed time is required")
  @field:Min(value = 1, message = "Elapsed time must be > 0")
  val elapsedTime: Int?,

  @field:NotNull(message = "Moving time is required")
  @field:Min(value = 1, message = "Moving time must be > 0")
  val movingTime: Int?,

  val description: String? = null,

  @field:DecimalMin(value = "0", message = "Total elevation gain must be >= 0")
  val totalElevationGain: BigDecimal? = null,

  @field:DecimalMin(value = "0", message = "Elevation high must be >= 0")
  val elevHigh: BigDecimal? = null,

  @field:DecimalMin(value = "0", message = "Elevation low must be >= 0")
  val elevLow: BigDecimal? = null,

  @field:DecimalMin(value = "0", message = "Average speed must be >= 0")
  val averageSpeed: BigDecimal? = null,

  @field:DecimalMin(value = "0", message = "Max speed must be >= 0")
  val maxSpeed: BigDecimal? = null,

  @field:Min(value = 0, message = "Average heartrate must be >= 0")
  val averageHeartrate: Int? = null,

  @field:Min(value = 0, message = "Max heartrate must be >= 0")
  val maxHeartrate: Int? = null,

  val hasHeartrate: Boolean = false,

  @field:Min(value = 0, message = "Average cadence must be >= 0")
  val averageCadence: Int? = null,

  @field:Min(value = 0, message = "Average watts must be >= 0")
  val averageWatts: Int? = null,

  @field:Min(value = 0, message = "Max watts must be >= 0")
  val maxWatts: Int? = null,

  @field:DecimalMin(value = "0", message = "Kilojoules must be >= 0")
  val kilojoules: BigDecimal? = null,

  @field:Min(value = 0, message = "Calories must be >= 0")
  val calories: Int? = null,
)
