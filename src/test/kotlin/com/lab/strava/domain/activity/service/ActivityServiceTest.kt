package com.lab.strava.domain.activity.service

import com.lab.strava.common.exception.EntityNotFoundException
import com.lab.strava.domain.activity.dto.CreateActivityRequest
import com.lab.strava.domain.activity.jpa.ActivityEntity
import com.lab.strava.domain.activity.jpa.ActivityRepository
import com.lab.strava.domain.activity.model.Activity
import com.lab.strava.domain.activity.model.ActivityType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class ActivityServiceTest {
  @Mock
  private lateinit var activityRepository: ActivityRepository

  @InjectMocks
  private lateinit var activityService: ActivityService

  @Nested
  inner class CreateActivity {
    @Test
    fun `should create activity with all fields`() {
      val request =
        CreateActivityRequest(
          name = "Morning Run",
          type = ActivityType.RUN,
          startDate = Instant.parse("2025-01-15T06:30:00Z"),
          startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
          timezone = "Europe/Prague",
          distance = BigDecimal("10000"),
          elapsedTime = 3600,
          movingTime = 3400,
          description = "Great morning run",
          totalElevationGain = BigDecimal("150"),
          elevHigh = BigDecimal("320"),
          elevLow = BigDecimal("250"),
          averageSpeed = BigDecimal("2.77"),
          maxSpeed = BigDecimal("4.16"),
          averageHeartrate = 155,
          maxHeartrate = 178,
          hasHeartrate = true,
          averageCadence = 168,
          averageWatts = 185,
          maxWatts = 320,
          kilojoules = BigDecimal("945"),
          calories = 890,
        )

      val entityCaptor = argumentCaptor<ActivityEntity>()
      whenever(activityRepository.save(any())).thenAnswer { it.arguments[0] }

      val result = activityService.createActivity(request)

      verify(activityRepository).save(entityCaptor.capture())
      val savedEntity = entityCaptor.firstValue

      assertNotNull(result.id)
      assertEquals(request.name, result.name)
      assertEquals(request.type, result.type)
      assertEquals(request.startDate, result.startDate)
      assertEquals(request.startDateLocal, result.startDateLocal)
      assertEquals(request.timezone, result.timezone)
      assertEquals(request.distance, result.distance)
      assertEquals(request.elapsedTime, result.elapsedTime)
      assertEquals(request.movingTime, result.movingTime)
      assertEquals(request.description, result.description)
      assertEquals(request.totalElevationGain, result.totalElevationGain)
      assertEquals(request.elevHigh, result.elevHigh)
      assertEquals(request.elevLow, result.elevLow)
      assertEquals(request.averageSpeed, result.averageSpeed)
      assertEquals(request.maxSpeed, result.maxSpeed)
      assertEquals(request.averageHeartrate, result.averageHeartrate)
      assertEquals(request.maxHeartrate, result.maxHeartrate)
      assertEquals(request.hasHeartrate, result.hasHeartrate)
      assertEquals(request.averageCadence, result.averageCadence)
      assertEquals(request.averageWatts, result.averageWatts)
      assertEquals(request.maxWatts, result.maxWatts)
      assertEquals(request.kilojoules, result.kilojoules)
      assertEquals(request.calories, result.calories)
      assertNotNull(result.createdAt)
      assertNotNull(result.updatedAt)
    }

    @Test
    fun `should create activity with minimal fields`() {
      val request =
        CreateActivityRequest(
          name = "Quick Walk",
          type = ActivityType.WALK,
          startDate = Instant.parse("2025-01-15T06:30:00Z"),
          startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
          timezone = "Europe/Prague",
          distance = BigDecimal("5000"),
          elapsedTime = 2400,
          movingTime = 2300,
        )

      whenever(activityRepository.save(any())).thenAnswer { it.arguments[0] }

      val result = activityService.createActivity(request)

      assertNotNull(result.id)
      assertEquals(request.name, result.name)
      assertEquals(request.type, result.type)
      assertEquals(request.distance, result.distance)
      assertEquals(null, result.description)
      assertEquals(null, result.totalElevationGain)
      assertEquals(null, result.averageHeartrate)
      assertEquals(false, result.hasHeartrate)
    }

    @Test
    fun `should handle nullable fields correctly`() {
      val request =
        CreateActivityRequest(
          name = "Test Activity",
          type = ActivityType.RUN,
          startDate = Instant.parse("2025-01-15T06:30:00Z"),
          startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
          timezone = "Europe/Prague",
          distance = BigDecimal("10000"),
          elapsedTime = 3600,
          movingTime = 3400,
          description = null,
          totalElevationGain = null,
          elevHigh = null,
          elevLow = null,
          averageSpeed = null,
          maxSpeed = null,
          averageHeartrate = null,
          maxHeartrate = null,
          hasHeartrate = false,
          averageCadence = null,
          averageWatts = null,
          maxWatts = null,
          kilojoules = null,
          calories = null,
        )

      whenever(activityRepository.save(any())).thenAnswer { it.arguments[0] }

      val result = activityService.createActivity(request)

      assertEquals(null, result.description)
      assertEquals(null, result.totalElevationGain)
      assertEquals(null, result.elevHigh)
      assertEquals(null, result.elevLow)
      assertEquals(null, result.averageSpeed)
      assertEquals(null, result.maxSpeed)
      assertEquals(null, result.averageHeartrate)
      assertEquals(null, result.maxHeartrate)
      assertEquals(false, result.hasHeartrate)
      assertEquals(null, result.averageCadence)
      assertEquals(null, result.averageWatts)
      assertEquals(null, result.maxWatts)
      assertEquals(null, result.kilojoules)
      assertEquals(null, result.calories)
    }
  }

  @Nested
  inner class GetActivityById {
    @Test
    fun `should return activity when found`() {
      val activityId = UUID.randomUUID()
      val activity = createTestActivity(activityId)
      val entity = ActivityEntity.fromDomain(activity)

      whenever(activityRepository.findById(activityId)).thenReturn(Optional.of(entity))

      val result = activityService.getActivityById(activityId)

      assertEquals(activityId, result.id)
      assertEquals(activity.name, result.name)
      assertEquals(activity.type, result.type)
    }

    @Test
    fun `should throw EntityNotFoundException when activity not found`() {
      val activityId = UUID.randomUUID()

      whenever(activityRepository.findById(activityId)).thenReturn(Optional.empty())

      val exception =
        assertThrows<EntityNotFoundException> {
          activityService.getActivityById(activityId)
        }

      assert(exception.message?.contains("Activity") == true)
      assert(exception.message?.contains(activityId.toString()) == true)
    }
  }

  @Nested
  inner class GetAllActivities {
    @Test
    fun `should return empty list when no activities exist`() {
      whenever(activityRepository.findAll()).thenReturn(emptyList())

      val result = activityService.getAllActivities()

      assertEquals(0, result.size)
    }

    @Test
    fun `should return all activities`() {
      val activity1 = createTestActivity(UUID.randomUUID(), "Activity 1")
      val activity2 = createTestActivity(UUID.randomUUID(), "Activity 2")
      val activity3 = createTestActivity(UUID.randomUUID(), "Activity 3")

      val entities =
        listOf(
          ActivityEntity.fromDomain(activity1),
          ActivityEntity.fromDomain(activity2),
          ActivityEntity.fromDomain(activity3),
        )

      whenever(activityRepository.findAll()).thenReturn(entities)

      val result = activityService.getAllActivities()

      assertEquals(3, result.size)
      assertEquals("Activity 1", result[0].name)
      assertEquals("Activity 2", result[1].name)
      assertEquals("Activity 3", result[2].name)
    }
  }

  private fun createTestActivity(
    id: UUID,
    name: String = "Test Activity",
  ): Activity =
    Activity(
      id = id,
      name = name,
      type = ActivityType.RUN,
      startDate = Instant.parse("2025-01-15T06:30:00Z"),
      startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
      timezone = "Europe/Prague",
      distance = BigDecimal("10000"),
      elapsedTime = 3600,
      movingTime = 3400,
      description = "Test description",
      totalElevationGain = BigDecimal("150"),
      elevHigh = BigDecimal("320"),
      elevLow = BigDecimal("250"),
      averageSpeed = BigDecimal("2.77"),
      maxSpeed = BigDecimal("4.16"),
      averageHeartrate = 155,
      maxHeartrate = 178,
      hasHeartrate = true,
      averageCadence = 168,
      averageWatts = 185,
      maxWatts = 320,
      kilojoules = BigDecimal("945"),
      calories = 890,
      createdAt = Instant.now(),
      updatedAt = Instant.now(),
    )
}
