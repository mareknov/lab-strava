package com.lab.strava.domain.user.model

import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserTest {

  @Test
  fun `should create user with all fields`() {
    val id = UUID.randomUUID()
    val now = Instant.now()

    val user = User(
      id = id,
      name = "John Doe",
      email = "john@example.com",
      firstName = "John",
      lastName = "Doe",
      stravaId = 12345L,
      avatarUrl = "https://example.com/avatar.jpg",
      isActive = true,
      createdAt = now,
      updatedAt = now
    )

    assertEquals(id, user.id)
    assertEquals("John Doe", user.name)
    assertEquals("john@example.com", user.email)
    assertEquals("John", user.firstName)
    assertEquals("Doe", user.lastName)
    assertEquals(12345L, user.stravaId)
    assertEquals("https://example.com/avatar.jpg", user.avatarUrl)
    assertTrue(user.isActive)
    assertEquals(now, user.createdAt)
    assertEquals(now, user.updatedAt)
  }

  @Test
  fun `should create user with minimal fields`() {
    val id = UUID.randomUUID()
    val now = Instant.now()

    val user = User(
      id = id,
      name = "Jane Doe",
      email = "jane@example.com",
      firstName = null,
      lastName = null,
      stravaId = null,
      avatarUrl = null,
      isActive = true,
      createdAt = now,
      updatedAt = now
    )

    assertEquals(id, user.id)
    assertEquals("Jane Doe", user.name)
    assertEquals("jane@example.com", user.email)
    assertNull(user.firstName)
    assertNull(user.lastName)
    assertNull(user.stravaId)
    assertNull(user.avatarUrl)
    assertTrue(user.isActive)
  }

  @Test
  fun `should support inactive user`() {
    val user = User(
      id = UUID.randomUUID(),
      name = "Inactive User",
      email = "inactive@example.com",
      firstName = null,
      lastName = null,
      stravaId = null,
      avatarUrl = null,
      isActive = false,
      createdAt = Instant.now(),
      updatedAt = Instant.now()
    )

    assertFalse(user.isActive)
  }

  @Test
  fun `should implement equals and hashCode correctly`() {
    val id = UUID.randomUUID()
    val now = Instant.now()

    val user1 = User(
      id = id,
      name = "John Doe",
      email = "john@example.com",
      firstName = "John",
      lastName = "Doe",
      stravaId = 12345L,
      avatarUrl = "https://example.com/avatar.jpg",
      isActive = true,
      createdAt = now,
      updatedAt = now
    )

    val user2 = User(
      id = id,
      name = "John Doe",
      email = "john@example.com",
      firstName = "John",
      lastName = "Doe",
      stravaId = 12345L,
      avatarUrl = "https://example.com/avatar.jpg",
      isActive = true,
      createdAt = now,
      updatedAt = now
    )

    assertEquals(user1, user2)
    assertEquals(user1.hashCode(), user2.hashCode())
  }
}
