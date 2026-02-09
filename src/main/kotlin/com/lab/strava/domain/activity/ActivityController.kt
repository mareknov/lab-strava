package com.lab.strava.domain.activity

import com.lab.strava.domain.activity.dto.ActivityResponse
import com.lab.strava.domain.activity.dto.CreateActivityRequest
import com.lab.strava.domain.activity.model.ActivityUse
import com.lab.strava.domain.activity.validation.ActivityValidation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v1/activities")
class ActivityController(
  private val activityUse: ActivityUse,
) {
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun createActivity(
    @Valid @RequestBody request: CreateActivityRequest,
  ): ActivityResponse {
    ActivityValidation.validateCreateRequest(request)
    val activity = activityUse.createActivity(request)
    return ActivityResponse.fromDomain(activity)
  }

  @GetMapping("/{id}")
  fun getActivityById(
    @PathVariable id: UUID,
  ): ActivityResponse {
    val activity = activityUse.getActivityById(id)
    return ActivityResponse.fromDomain(activity)
  }

  @GetMapping
  fun getAllActivities(): List<ActivityResponse> {
    val activities = activityUse.getAllActivities()
    return activities.map { ActivityResponse.fromDomain(it) }
  }
}
