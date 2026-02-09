package com.lab.strava.domain.user.validation

import com.lab.strava.domain.user.dto.CreateUserRequest
import com.lab.strava.domain.user.dto.UpdateUserRequest

object UserValidation {
  fun validateCreateRequest(request: CreateUserRequest) {
    validateEmail(request.email)
    request.avatarUrl?.let { validateUrl(it, "avatarUrl") }
  }

  fun validateUpdateRequest(request: UpdateUserRequest) {
    request.email?.let { validateEmail(it) }
    request.avatarUrl?.let { validateUrl(it, "avatarUrl") }
  }

  private fun validateEmail(email: String) {
    val normalizedEmail = email.lowercase().trim()
    if (normalizedEmail != email) {
      throw IllegalArgumentException("Email should be lowercase and trimmed")
    }
  }

  private fun validateUrl(
    url: String,
    fieldName: String,
  ) {
    if (url.isNotBlank() && !url.startsWith("http://") && !url.startsWith("https://")) {
      throw IllegalArgumentException("$fieldName must be a valid HTTP or HTTPS URL")
    }
  }
}
