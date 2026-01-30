package com.lab.strava.domain.user.model

import com.lab.strava.domain.user.dto.CreateUserRequest
import com.lab.strava.domain.user.dto.UpdateUserRequest
import java.util.UUID

interface UserUse {
  fun createUser(request: CreateUserRequest): User

  fun getUserById(id: UUID): User

  fun getAllUsers(): List<User>

  fun updateUser(
    id: UUID,
    request: UpdateUserRequest,
  ): User

  fun deactivateUser(id: UUID): User
}
