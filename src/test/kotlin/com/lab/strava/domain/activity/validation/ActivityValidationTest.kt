package com.lab.strava.domain.activity.validation

import com.lab.strava.domain.activity.dto.CreateActivityRequest
import com.lab.strava.domain.activity.model.ActivityType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant

class ActivityValidationTest {
  @Test
  fun `should pass validation for valid request with all fields`() {
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
        description = "Test",
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

    assertDoesNotThrow {
      ActivityValidation.validateCreateRequest(request)
    }
  }

  @Test
  fun `should pass validation for valid request with minimal fields`() {
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
      )

    assertDoesNotThrow {
      ActivityValidation.validateCreateRequest(request)
    }
  }

  @Test
  fun `should fail when distance is negative`() {
    val request =
      CreateActivityRequest(
        name = "Test Activity",
        type = ActivityType.RUN,
        startDate = Instant.parse("2025-01-15T06:30:00Z"),
        startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
        timezone = "Europe/Prague",
        distance = BigDecimal("-100"),
        elapsedTime = 3600,
        movingTime = 3400,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Distance must be >= 0"))
  }

  @Test
  fun `should fail when elapsed time is zero`() {
    val request =
      CreateActivityRequest(
        name = "Test Activity",
        type = ActivityType.RUN,
        startDate = Instant.parse("2025-01-15T06:30:00Z"),
        startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
        timezone = "Europe/Prague",
        distance = BigDecimal("10000"),
        elapsedTime = 0,
        movingTime = 3400,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Elapsed time must be > 0"))
  }

  @Test
  fun `should fail when moving time is zero`() {
    val request =
      CreateActivityRequest(
        name = "Test Activity",
        type = ActivityType.RUN,
        startDate = Instant.parse("2025-01-15T06:30:00Z"),
        startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
        timezone = "Europe/Prague",
        distance = BigDecimal("10000"),
        elapsedTime = 3600,
        movingTime = 0,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Moving time must be > 0"))
  }

  @Test
  fun `should fail when moving time exceeds elapsed time`() {
    val request =
      CreateActivityRequest(
        name = "Test Activity",
        type = ActivityType.RUN,
        startDate = Instant.parse("2025-01-15T06:30:00Z"),
        startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
        timezone = "Europe/Prague",
        distance = BigDecimal("10000"),
        elapsedTime = 3000,
        movingTime = 3400,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Moving time must be <= elapsed time"))
  }

  @Test
  fun `should fail when elevation high is not greater than elevation low`() {
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
        elevHigh = BigDecimal("250"),
        elevLow = BigDecimal("320"),
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Elevation high must be > elevation low"))
  }

  @Test
  fun `should fail when max speed is less than average speed`() {
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
        averageSpeed = BigDecimal("5.00"),
        maxSpeed = BigDecimal("4.00"),
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Max speed must be >= average speed"))
  }

  @Test
  fun `should fail when average heartrate is below zero`() {
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
        averageHeartrate = -1,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Average heartrate must be between 0 and 300"))
  }

  @Test
  fun `should fail when average heartrate exceeds 300`() {
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
        averageHeartrate = 301,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Average heartrate must be between 0 and 300"))
  }

  @Test
  fun `should fail when max heartrate exceeds 300`() {
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
        maxHeartrate = 350,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Max heartrate must be between 0 and 300"))
  }

  @Test
  fun `should fail when timezone is invalid`() {
    val request =
      CreateActivityRequest(
        name = "Test Activity",
        type = ActivityType.RUN,
        startDate = Instant.parse("2025-01-15T06:30:00Z"),
        startDateLocal = Instant.parse("2025-01-15T07:30:00Z"),
        timezone = "Invalid/Timezone",
        distance = BigDecimal("10000"),
        elapsedTime = 3600,
        movingTime = 3400,
      )

    val exception =
      assertThrows<IllegalArgumentException> {
        ActivityValidation.validateCreateRequest(request)
      }
    assertTrue(exception.message!!.contains("Invalid timezone"))
  }
}
