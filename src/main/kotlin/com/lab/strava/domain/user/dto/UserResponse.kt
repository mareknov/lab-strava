package com.lab.strava.domain.user.dto

import com.lab.strava.domain.user.model.User
import java.time.Instant
import java.util.UUID

data class UserResponse(
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
) {
  companion object {
    fun fromDomain(user: User): UserResponse =
      UserResponse(
        id = user.id,
        name = user.name,
        email = user.email,
        firstName = user.firstName,
        lastName = user.lastName,
        stravaId = user.stravaId,
        avatarUrl = user.avatarUrl,
        isActive = user.isActive,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt,
      )
  }
}
