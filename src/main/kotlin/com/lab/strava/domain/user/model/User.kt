package com.lab.strava.domain.user.model

import java.time.Instant
import java.util.UUID

data class User(
  val id: UUID,
  val name: String,
  val email: String,
  val firstName: String?,
  val lastName: String?,
  val stravaId: Long?,
  val avatarUrl: String?,
  val isActive: Boolean,
  val createdAt: Instant,
  val updatedAt: Instant,
)
