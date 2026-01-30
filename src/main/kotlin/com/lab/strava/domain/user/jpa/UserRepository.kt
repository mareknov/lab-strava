package com.lab.strava.domain.user.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
  fun existsByEmail(email: String): Boolean
  fun existsByStravaId(stravaId: Long): Boolean
  fun findByEmail(email: String): UserEntity?
  fun findByStravaId(stravaId: Long): UserEntity?
}
