package com.lab.strava.domain.activity.model

import com.lab.strava.domain.activity.dto.CreateActivityRequest
import java.util.UUID

interface ActivityUse {
  fun createActivity(request: CreateActivityRequest): Activity

  fun getActivityById(id: UUID): Activity

  fun getAllActivities(): List<Activity>
}
