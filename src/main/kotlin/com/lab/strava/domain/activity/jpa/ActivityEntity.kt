package com.lab.strava.domain.activity.jpa

import com.lab.strava.domain.activity.model.Activity
import com.lab.strava.domain.activity.model.ActivityType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "activities")
class ActivityEntity(
  @Id
  @Column(name = "id")
  val id: UUID,

  @Column(name = "name", nullable = false)
  val name: String,

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  val type: ActivityType,

  @Column(name = "start_date", nullable = false)
  val startDate: Instant,

  @Column(name = "start_date_local", nullable = false)
  val startDateLocal: Instant,

  @Column(name = "timezone", nullable = false)
  val timezone: String,

  @Column(name = "distance", nullable = false, columnDefinition = "NUMERIC(12,2)")
  val distance: BigDecimal,

  @Column(name = "elapsed_time", nullable = false)
  val elapsedTime: Int,

  @Column(name = "moving_time", nullable = false)
  val movingTime: Int,

  @Column(name = "description")
  val description: String? = null,

  @Column(name = "total_elevation_gain", columnDefinition = "NUMERIC(10,2)")
  val totalElevationGain: BigDecimal? = null,

  @Column(name = "elev_high", columnDefinition = "NUMERIC(10,2)")
  val elevHigh: BigDecimal? = null,

  @Column(name = "elev_low", columnDefinition = "NUMERIC(10,2)")
  val elevLow: BigDecimal? = null,

  @Column(name = "average_speed", columnDefinition = "NUMERIC(8,4)")
  val averageSpeed: BigDecimal? = null,

  @Column(name = "max_speed", columnDefinition = "NUMERIC(8,4)")
  val maxSpeed: BigDecimal? = null,

  @Column(name = "average_heartrate")
  val averageHeartrate: Int? = null,

  @Column(name = "max_heartrate")
  val maxHeartrate: Int? = null,

  @Column(name = "has_heartrate", nullable = false)
  val hasHeartrate: Boolean = false,

  @Column(name = "average_cadence")
  val averageCadence: Int? = null,

  @Column(name = "average_watts")
  val averageWatts: Int? = null,

  @Column(name = "max_watts")
  val maxWatts: Int? = null,

  @Column(name = "kilojoules", columnDefinition = "NUMERIC(10,2)")
  val kilojoules: BigDecimal? = null,

  @Column(name = "calories")
  val calories: Int? = null,

  @Column(name = "created_at", nullable = false)
  val createdAt: Instant,

  @Column(name = "updated_at", nullable = false)
  val updatedAt: Instant,
) {
  fun toDomain(): Activity =
    Activity(
      id = id,
      name = name,
      type = type,
      startDate = startDate,
      startDateLocal = startDateLocal,
      timezone = timezone,
      distance = distance,
      elapsedTime = elapsedTime,
      movingTime = movingTime,
      description = description,
      totalElevationGain = totalElevationGain,
      elevHigh = elevHigh,
      elevLow = elevLow,
      averageSpeed = averageSpeed,
      maxSpeed = maxSpeed,
      averageHeartrate = averageHeartrate,
      maxHeartrate = maxHeartrate,
      hasHeartrate = hasHeartrate,
      averageCadence = averageCadence,
      averageWatts = averageWatts,
      maxWatts = maxWatts,
      kilojoules = kilojoules,
      calories = calories,
      createdAt = createdAt,
      updatedAt = updatedAt,
    )

  companion object {
    fun fromDomain(activity: Activity): ActivityEntity =
      ActivityEntity(
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
