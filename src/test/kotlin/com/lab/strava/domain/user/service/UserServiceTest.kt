package com.lab.strava.domain.user.service

import com.lab.strava.common.exception.EntityNotFoundException
import com.lab.strava.domain.user.dto.CreateUserRequest
import com.lab.strava.domain.user.dto.UpdateUserRequest
import com.lab.strava.domain.user.jpa.UserEntity
import com.lab.strava.domain.user.jpa.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserServiceTest {
  private lateinit var userRepository: UserRepository
  private lateinit var userService: UserService

  @BeforeEach
  fun setUp() {
    userRepository = mock()
    userService = UserService(userRepository)
  }

  @Nested
  inner class CreateUser {
    @Test
    fun `should create user with all fields`() {
      val savedEntity =
        UserEntity(
          name = "John Doe",
          email = "john@example.com",
          firstName = "John",
          lastName = "Doe",
          stravaId = 12345L,
          avatarUrl = "https://example.com/avatar.jpg",
        )
      whenever(userRepository.existsByEmail("john@example.com")).thenReturn(false)
      whenever(userRepository.existsByStravaId(12345L)).thenReturn(false)
      whenever(userRepository.save(any<UserEntity>())).thenReturn(savedEntity)

      val request =
        CreateUserRequest(
          name = "John Doe",
          email = "john@example.com",
          firstName = "John",
          lastName = "Doe",
          stravaId = 12345L,
          avatarUrl = "https://example.com/avatar.jpg",
        )
      val user = userService.createUser(request)

      assertNotNull(user)
      assertEquals("John Doe", user.name)
      assertEquals("john@example.com", user.email)
      assertEquals("John", user.firstName)
      assertEquals("Doe", user.lastName)
      assertEquals(12345L, user.stravaId)
      assertEquals("https://example.com/avatar.jpg", user.avatarUrl)
      assertTrue(user.isActive)
    }

    @Test
    fun `should create user with minimal fields`() {
      val savedEntity = UserEntity(name = "Jane Doe", email = "jane@example.com")
      whenever(userRepository.existsByEmail("jane@example.com")).thenReturn(false)
      whenever(userRepository.save(any<UserEntity>())).thenReturn(savedEntity)

      val request = CreateUserRequest(name = "Jane Doe", email = "jane@example.com")
      val user = userService.createUser(request)

      assertNotNull(user)
      assertEquals("Jane Doe", user.name)
      assertEquals("jane@example.com", user.email)
    }

    @Test
    fun `should throw exception when email already exists`() {
      whenever(userRepository.existsByEmail("existing@example.com")).thenReturn(true)

      val request = CreateUserRequest(name = "Test", email = "existing@example.com")
      val exception =
        assertThrows<IllegalArgumentException> {
          userService.createUser(request)
        }

      assertEquals("User with email 'existing@example.com' already exists", exception.message)
      verify(userRepository, never()).save(any())
    }

    @Test
    fun `should throw exception when stravaId already exists`() {
      whenever(userRepository.existsByEmail(any())).thenReturn(false)
      whenever(userRepository.existsByStravaId(12345L)).thenReturn(true)

      val request = CreateUserRequest(name = "Test", email = "test@example.com", stravaId = 12345L)
      val exception =
        assertThrows<IllegalArgumentException> {
          userService.createUser(request)
        }

      assertEquals("User with Strava ID '12345' already exists", exception.message)
      verify(userRepository, never()).save(any())
    }
  }

  @Nested
  inner class GetUserById {
    @Test
    fun `should return user when found`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "John Doe", email = "john@example.com")
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))

      val user = userService.getUserById(id)

      assertEquals(id, user.id)
      assertEquals("John Doe", user.name)
    }

    @Test
    fun `should throw EntityNotFoundException when user not found`() {
      val id = UUID.randomUUID()
      whenever(userRepository.findById(id)).thenReturn(Optional.empty())

      val exception =
        assertThrows<EntityNotFoundException> {
          userService.getUserById(id)
        }

      assertEquals("User", exception.entityType)
      assertEquals(id, exception.entityId)
    }
  }

  @Nested
  inner class GetAllUsers {
    @Test
    fun `should return empty list when no users`() {
      whenever(userRepository.findAll()).thenReturn(emptyList())

      val users = userService.getAllUsers()

      assertTrue(users.isEmpty())
    }

    @Test
    fun `should return all users`() {
      val entities =
        listOf(
          UserEntity(name = "User 1", email = "user1@example.com"),
          UserEntity(name = "User 2", email = "user2@example.com"),
        )
      whenever(userRepository.findAll()).thenReturn(entities)

      val users = userService.getAllUsers()

      assertEquals(2, users.size)
      assertEquals("User 1", users[0].name)
      assertEquals("User 2", users[1].name)
    }
  }

  @Nested
  inner class UpdateUser {
    @Test
    fun `should update user name`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Old Name", email = "test@example.com")
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

      val request = UpdateUserRequest(name = "New Name")
      val user = userService.updateUser(id, request)

      assertEquals("New Name", user.name)
    }

    @Test
    fun `should update user email when unique`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "old@example.com")
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.existsByEmail("new@example.com")).thenReturn(false)
      whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

      val request = UpdateUserRequest(email = "new@example.com")
      val user = userService.updateUser(id, request)

      assertEquals("new@example.com", user.email)
    }

    @Test
    fun `should throw exception when updating to existing email`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "current@example.com")
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.existsByEmail("taken@example.com")).thenReturn(true)

      val request = UpdateUserRequest(email = "taken@example.com")
      val exception =
        assertThrows<IllegalArgumentException> {
          userService.updateUser(id, request)
        }

      assertEquals("User with email 'taken@example.com' already exists", exception.message)
    }

    @Test
    fun `should allow updating to same email`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "same@example.com")
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

      val request = UpdateUserRequest(email = "same@example.com")
      val user = userService.updateUser(id, request)

      assertEquals("same@example.com", user.email)
    }

    @Test
    fun `should update user stravaId when unique`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "test@example.com", stravaId = 11111L)
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.existsByStravaId(22222L)).thenReturn(false)
      whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

      val request = UpdateUserRequest(stravaId = 22222L)
      val user = userService.updateUser(id, request)

      assertEquals(22222L, user.stravaId)
    }

    @Test
    fun `should throw exception when updating to existing stravaId`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "test@example.com", stravaId = 11111L)
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.existsByStravaId(99999L)).thenReturn(true)

      val request = UpdateUserRequest(stravaId = 99999L)
      val exception =
        assertThrows<IllegalArgumentException> {
          userService.updateUser(id, request)
        }

      assertEquals("User with Strava ID '99999' already exists", exception.message)
    }

    @Test
    fun `should allow updating to same stravaId`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "test@example.com", stravaId = 12345L)
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

      val request = UpdateUserRequest(stravaId = 12345L)
      val user = userService.updateUser(id, request)

      assertEquals(12345L, user.stravaId)
      verify(userRepository, never()).existsByStravaId(any())
    }

    @Test
    fun `should throw EntityNotFoundException when user not found`() {
      val id = UUID.randomUUID()
      whenever(userRepository.findById(id)).thenReturn(Optional.empty())

      val request = UpdateUserRequest(name = "New Name")
      val exception =
        assertThrows<EntityNotFoundException> {
          userService.updateUser(id, request)
        }

      assertEquals("User", exception.entityType)
    }
  }

  @Nested
  inner class DeactivateUser {
    @Test
    fun `should deactivate user and return updated user`() {
      val id = UUID.randomUUID()
      val entity = UserEntity(id = id, name = "Test", email = "test@example.com", isActive = true)
      whenever(userRepository.findById(id)).thenReturn(Optional.of(entity))
      whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

      val user = userService.deactivateUser(id)

      assertFalse(user.isActive)
      verify(userRepository).save(any())
    }

    @Test
    fun `should throw EntityNotFoundException when user not found`() {
      val id = UUID.randomUUID()
      whenever(userRepository.findById(id)).thenReturn(Optional.empty())

      val exception =
        assertThrows<EntityNotFoundException> {
          userService.deactivateUser(id)
        }

      assertEquals("User", exception.entityType)
    }
  }
}
