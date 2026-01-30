package com.lab.strava.domain.user.model

import java.util.UUID

interface UserUse {
  fun createUser(
    name: String,
    email: String,
    firstName: String? = null,
    lastName: String? = null,
    stravaId: Long? = null,
    avatarUrl: String? = null,
  ): User

  fun getUserById(id: UUID): User

  fun getAllUsers(): List<User>

  fun updateUser(
    id: UUID,
    name: String? = null,
    email: String? = null,
    firstName: String? = null,
    lastName: String? = null,
    stravaId: Long? = null,
    avatarUrl: String? = null,
    isActive: Boolean? = null,
  ): User

  fun deleteUser(id: UUID)
}
