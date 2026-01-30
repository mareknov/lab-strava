package com.lab.strava.domain.user.service

import com.lab.strava.common.exception.EntityNotFoundException
import com.lab.strava.domain.user.dto.CreateUserRequest
import com.lab.strava.domain.user.dto.UpdateUserRequest
import com.lab.strava.domain.user.jpa.UserEntity
import com.lab.strava.domain.user.jpa.UserRepository
import com.lab.strava.domain.user.model.User
import com.lab.strava.domain.user.model.UserUse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class UserService(
  private val userRepository: UserRepository,
) : UserUse {
  override fun createUser(request: CreateUserRequest): User {
    if (userRepository.existsByEmail(request.email)) {
      throw IllegalArgumentException("User with email '${request.email}' already exists")
    }
    if (request.stravaId != null && userRepository.existsByStravaId(request.stravaId)) {
      throw IllegalArgumentException("User with Strava ID '${request.stravaId}' already exists")
    }

    val entity =
      UserEntity(
        name = request.name,
        email = request.email,
        firstName = request.firstName,
        lastName = request.lastName,
        stravaId = request.stravaId,
        avatarUrl = request.avatarUrl,
      )
    return userRepository.save(entity).toDomain()
  }

  @Transactional(readOnly = true)
  override fun getUserById(id: UUID): User =
    userRepository
      .findById(id)
      .orElseThrow { EntityNotFoundException("User", id) }
      .toDomain()

  @Transactional(readOnly = true)
  override fun getAllUsers(): List<User> = userRepository.findAll().map { it.toDomain() }

  override fun updateUser(
    id: UUID,
    request: UpdateUserRequest,
  ): User {
    val entity =
      userRepository
        .findById(id)
        .orElseThrow { EntityNotFoundException("User", id) }

    if (request.email != null && request.email != entity.email && userRepository.existsByEmail(request.email)) {
      throw IllegalArgumentException("User with email '${request.email}' already exists")
    }
    if (request.stravaId != null && request.stravaId != entity.stravaId &&
      userRepository.existsByStravaId(request.stravaId)
    ) {
      throw IllegalArgumentException("User with Strava ID '${request.stravaId}' already exists")
    }

    request.name?.let { entity.name = it }
    request.email?.let { entity.email = it }
    request.firstName?.let { entity.firstName = it }
    request.lastName?.let { entity.lastName = it }
    request.stravaId?.let { entity.stravaId = it }
    request.avatarUrl?.let { entity.avatarUrl = it }
    entity.updatedAt = Instant.now()

    return userRepository.save(entity).toDomain()
  }

  override fun deactivateUser(id: UUID): User {
    val entity =
      userRepository
        .findById(id)
        .orElseThrow { EntityNotFoundException("User", id) }

    entity.isActive = false
    entity.updatedAt = Instant.now()

    return userRepository.save(entity).toDomain()
  }
}
