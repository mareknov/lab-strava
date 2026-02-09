package com.lab.strava.domain.activity

import com.lab.strava.common.exception.EntityNotFoundException
import com.lab.strava.common.exception.GlobalExceptionHandler
import com.lab.strava.domain.activity.dto.CreateActivityRequest
import com.lab.strava.domain.activity.model.Activity
import com.lab.strava.domain.activity.model.ActivityType
import com.lab.strava.domain.activity.model.ActivityUse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import java.util.stream.Stream

@WebMvcTest(ActivityController::class)
@Import(GlobalExceptionHandler::class)
class ActivityControllerTest {
  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockitoBean
  private lateinit var activityUse: ActivityUse

  @Nested
  inner class CreateActivity {
    @Test
    fun `should create activity with all fields and return 201`() {
      val activity = createTestActivity(name = "Morning Run")
      whenever(activityUse.createActivity(any())).thenReturn(activity)

      mockMvc
        .perform(
          post("/v1/activities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
              """
              {
                "name": "Morning Run",
                "type": "RUN",
                "startDate": "2025-01-15T06:30:00Z",
                "startDateLocal": "2025-01-15T07:30:00Z",
                "timezone": "Europe/Prague",
                "distance": 10000.00,
                "elapsedTime": 3600,
                "movingTime": 3400,
                "description": "Great morning run",
                "totalElevationGain": 150.50,
                "elevHigh": 320.00,
                "elevLow": 250.00,
                "averageSpeed": 2.7778,
                "maxSpeed": 4.1667,
                "averageHeartrate": 155,
                "maxHeartrate": 178,
                "hasHeartrate": true,
                "averageCadence": 168,
                "averageWatts": 185,
                "maxWatts": 320,
                "kilojoules": 945.00,
                "calories": 890
              }
              """.trimIndent(),
            ),
        ).andExpect(status().isCreated)
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Morning Run"))
        .andExpect(jsonPath("$.type").value("RUN"))
        .andExpect(jsonPath("$.distance").value(10000))
        .andExpect(jsonPath("$.elapsedTime").value(3600))
        .andExpect(jsonPath("$.movingTime").value(3400))
        .andExpect(jsonPath("$.description").value("Test description"))
        .andExpect(jsonPath("$.hasHeartrate").value(true))
    }

    @Test
    fun `should create activity with minimal fields and return 201`() {
      val activity = createTestActivity(name = "Quick Walk")
      whenever(activityUse.createActivity(any())).thenReturn(activity)

      mockMvc
        .perform(
          post("/v1/activities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
              """
              {
                "name": "Quick Walk",
                "type": "WALK",
                "startDate": "2025-01-15T06:30:00Z",
                "startDateLocal": "2025-01-15T07:30:00Z",
                "timezone": "Europe/Prague",
                "distance": 5000.00,
                "elapsedTime": 2400,
                "movingTime": 2300
              }
              """.trimIndent(),
            ),
        ).andExpect(status().isCreated)
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Quick Walk"))
    }

    @ParameterizedTest
    @MethodSource("com.lab.strava.domain.activity.ActivityControllerTest#invalidCreateRequests")
    fun `should return 400 for invalid requests`(requestBody: String) {
      mockMvc
        .perform(
          post("/v1/activities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody),
        ).andExpect(status().isBadRequest)
    }
  }

  @Nested
  inner class GetActivityById {
    @Test
    fun `should return activity when found`() {
      val activityId = UUID.randomUUID()
      val activity = createTestActivity(activityId)

      whenever(activityUse.getActivityById(activityId)).thenReturn(activity)

      mockMvc
        .perform(get("/v1/activities/$activityId"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id").value(activityId.toString()))
        .andExpect(jsonPath("$.name").value("Test Activity"))
        .andExpect(jsonPath("$.type").value("RUN"))
    }

    @Test
    fun `should return 404 when activity not found`() {
      val activityId = UUID.randomUUID()

      whenever(activityUse.getActivityById(activityId))
        .thenThrow(EntityNotFoundException("Activity", activityId))

      mockMvc
        .perform(get("/v1/activities/$activityId"))
        .andExpect(status().isNotFound)
    }
  }

  @Nested
  inner class GetAllActivities {
    @Test
    fun `should return empty list when no activities exist`() {
      whenever(activityUse.getAllActivities()).thenReturn(emptyList())

      mockMvc
        .perform(get("/v1/activities"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$").isArray)
        .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `should return all activities`() {
      val activity1 = createTestActivity(UUID.randomUUID(), "Activity 1")
      val activity2 = createTestActivity(UUID.randomUUID(), "Activity 2")
      val activity3 = createTestActivity(UUID.randomUUID(), "Activity 3")

      whenever(activityUse.getAllActivities()).thenReturn(listOf(activity1, activity2, activity3))

      mockMvc
        .perform(get("/v1/activities"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$").isArray)
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].name").value("Activity 1"))
        .andExpect(jsonPath("$[1].name").value("Activity 2"))
        .andExpect(jsonPath("$[2].name").value("Activity 3"))
    }
  }

  private fun createTestActivity(
    id: UUID = UUID.randomUUID(),
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

  companion object {
    @JvmStatic
    fun invalidCreateRequests(): Stream<String> =
      Stream.of(
        """{"name": "", "type": "RUN", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "distance": 10000, "elapsedTime": 3600, "movingTime": 3400}""",
        """{"name": "Test", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "distance": 10000, "elapsedTime": 3600, "movingTime": 3400}""",
        """{"name": "Test", "type": "RUN", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "distance": 10000, "elapsedTime": 3600, "movingTime": 3400}""",
        """{"name": "Test", "type": "RUN", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "elapsedTime": 3600, "movingTime": 3400}""",
        """{"name": "Test", "type": "RUN", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "distance": -100, "elapsedTime": 3600, "movingTime": 3400}""",
        """{"name": "Test", "type": "RUN", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "distance": 10000, "movingTime": 3400}""",
        """{"name": "Test", "type": "RUN", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Europe/Prague", "distance": 10000, "elapsedTime": 3000, "movingTime": 3400}""",
        """{"name": "Test", "type": "RUN", "startDate": "2025-01-15T06:30:00Z", "startDateLocal": "2025-01-15T07:30:00Z", "timezone": "Invalid/Timezone", "distance": 10000, "elapsedTime": 3600, "movingTime": 3400}""",
      )
  }
}
