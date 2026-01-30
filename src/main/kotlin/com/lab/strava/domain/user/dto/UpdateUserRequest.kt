package com.lab.strava.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
  @field:Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
  val name: String? = null,

  @field:Email(message = "Email must be valid")
  val email: String? = null,

  @field:Size(max = 255, message = "First name must be at most 255 characters")
  val firstName: String? = null,

  @field:Size(max = 255, message = "Last name must be at most 255 characters")
  val lastName: String? = null,

  val stravaId: Long? = null,

  @field:Size(max = 2048, message = "Avatar URL must be at most 2048 characters")
  val avatarUrl: String? = null,

  val isActive: Boolean? = null,
)
