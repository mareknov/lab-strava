package com.lab.strava.domain.activity.service

import com.lab.strava.common.exception.EntityNotFoundException
import com.lab.strava.domain.activity.dto.CreateActivityRequest
import com.lab.strava.domain.activity.jpa.ActivityEntity
import com.lab.strava.domain.activity.jpa.ActivityRepository
import com.lab.strava.domain.activity.model.Activity
import com.lab.strava.domain.activity.model.ActivityUse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class ActivityService(
  private val activityRepository: ActivityRepository,
) : ActivityUse {
  override fun createActivity(request: CreateActivityRequest): Activity {
    val now = Instant.now()
    val activity =
      Activity(
        id = UUID.randomUUID(),
        name = request.name,
        type = request.type!!,
        startDate = request.startDate!!,
        startDateLocal = request.startDateLocal!!,
        timezone = request.timezone,
        distance = request.distance!!,
        elapsedTime = request.elapsedTime!!,
        movingTime = request.movingTime!!,
        description = request.description,
        totalElevationGain = request.totalElevationGain,
        elevHigh = request.elevHigh,
        elevLow = request.elevLow,
        averageSpeed = request.averageSpeed,
        maxSpeed = request.maxSpeed,
        averageHeartrate = request.averageHeartrate,
        maxHeartrate = request.maxHeartrate,
        hasHeartrate = request.hasHeartrate,
        averageCadence = request.averageCadence,
        averageWatts = request.averageWatts,
        maxWatts = request.maxWatts,
        kilojoules = request.kilojoules,
        calories = request.calories,
        createdAt = now,
        updatedAt = now,
      )

    val entity = ActivityEntity.fromDomain(activity)
    return activityRepository.save(entity).toDomain()
  }

  @Transactional(readOnly = true)
  override fun getActivityById(id: UUID): Activity =
    activityRepository
      .findById(id)
      .map { it.toDomain() }
      .orElseThrow { EntityNotFoundException("Activity", id) }

  @Transactional(readOnly = true)
  override fun getAllActivities(): List<Activity> =
    activityRepository
      .findAll()
      .map { it.toDomain() }
}
