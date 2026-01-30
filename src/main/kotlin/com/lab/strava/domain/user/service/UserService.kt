package com.lab.strava.domain.user.service

import com.lab.strava.common.exception.EntityNotFoundException
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
  private val userRepository: UserRepository
) : UserUse {

  override fun createUser(
    name: String,
    email: String,
    firstName: String?,
    lastName: String?,
    stravaId: Long?,
    avatarUrl: String?
  ): User {
    if (userRepository.existsByEmail(email)) {
      throw IllegalArgumentException("User with email '$email' already exists")
    }
    if (stravaId != null && userRepository.existsByStravaId(stravaId)) {
      throw IllegalArgumentException("User with Strava ID '$stravaId' already exists")
    }

    val entity = UserEntity(
      name = name,
      email = email,
      firstName = firstName,
      lastName = lastName,
      stravaId = stravaId,
      avatarUrl = avatarUrl
    )
    return userRepository.save(entity).toDomain()
  }

  @Transactional(readOnly = true)
  override fun getUserById(id: UUID): User {
    return userRepository.findById(id)
      .orElseThrow { EntityNotFoundException("User", id) }
      .toDomain()
  }

  @Transactional(readOnly = true)
  override fun getAllUsers(): List<User> {
    return userRepository.findAll().map { it.toDomain() }
  }

  override fun updateUser(
    id: UUID,
    name: String?,
    email: String?,
    firstName: String?,
    lastName: String?,
    stravaId: Long?,
    avatarUrl: String?,
    isActive: Boolean?
  ): User {
    val entity = userRepository.findById(id)
      .orElseThrow { EntityNotFoundException("User", id) }

    if (email != null && email != entity.email && userRepository.existsByEmail(email)) {
      throw IllegalArgumentException("User with email '$email' already exists")
    }
    if (stravaId != null && stravaId != entity.stravaId && userRepository.existsByStravaId(stravaId)) {
      throw IllegalArgumentException("User with Strava ID '$stravaId' already exists")
    }

    name?.let { entity.name = it }
    email?.let { entity.email = it }
    firstName?.let { entity.firstName = it }
    lastName?.let { entity.lastName = it }
    stravaId?.let { entity.stravaId = it }
    avatarUrl?.let { entity.avatarUrl = it }
    isActive?.let { entity.isActive = it }
    entity.updatedAt = Instant.now()

    return userRepository.save(entity).toDomain()
  }

  override fun deleteUser(id: UUID) {
    if (!userRepository.existsById(id)) {
      throw EntityNotFoundException("User", id)
    }
    userRepository.deleteById(id)
  }
}
