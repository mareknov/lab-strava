package com.lab.strava.domain.user.jpa

import com.lab.strava.domain.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
  @Id
  val id: UUID = UUID.randomUUID(),

  @Column(nullable = false)
  var name: String,

  @Column(nullable = false, unique = true)
  var email: String,

  @Column(name = "first_name")
  var firstName: String? = null,

  @Column(name = "last_name")
  var lastName: String? = null,

  @Column(name = "strava_id", unique = true)
  var stravaId: Long? = null,

  @Column(name = "avatar_url")
  var avatarUrl: String? = null,

  @Column(name = "is_active", nullable = false)
  var isActive: Boolean = true,

  @Column(name = "created_at", nullable = false, updatable = false)
  val createdAt: Instant = Instant.now(),

  @Column(name = "updated_at", nullable = false)
  var updatedAt: Instant = Instant.now(),
) {
  fun toDomain(): User =
    User(
      id = id,
      name = name,
      email = email,
      firstName = firstName,
      lastName = lastName,
      stravaId = stravaId,
      avatarUrl = avatarUrl,
      isActive = isActive,
      createdAt = createdAt,
      updatedAt = updatedAt,
    )

  companion object {
    fun fromDomain(user: User): UserEntity =
      UserEntity(
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
