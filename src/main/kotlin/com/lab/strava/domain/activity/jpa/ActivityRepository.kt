package com.lab.strava.domain.activity.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityRepository : JpaRepository<ActivityEntity, UUID>
