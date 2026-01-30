package com.lab.strava.domain.user

import com.lab.strava.common.exception.EntityNotFoundException
import com.lab.strava.common.exception.GlobalExceptionHandler
import com.lab.strava.domain.user.model.User
import com.lab.strava.domain.user.model.UserUse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.util.UUID
import java.util.stream.Stream

@WebMvcTest(UserController::class)
@Import(GlobalExceptionHandler::class)
class UserControllerTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockitoBean
  private lateinit var userUse: UserUse

  companion object {
    private val TEST_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    private val TEST_TIME = Instant.parse("2024-01-15T10:30:00Z")

    private fun createTestUser(
      id: UUID = TEST_ID,
      name: String = "John Doe",
      email: String = "john@example.com",
      firstName: String? = "John",
      lastName: String? = "Doe",
      stravaId: Long? = 12345L,
      avatarUrl: String? = "https://example.com/avatar.jpg",
      isActive: Boolean = true
    ) = User(
      id = id,
      name = name,
      email = email,
      firstName = firstName,
      lastName = lastName,
      stravaId = stravaId,
      avatarUrl = avatarUrl,
      isActive = isActive,
      createdAt = TEST_TIME,
      updatedAt = TEST_TIME
    )

    @JvmStatic
    fun invalidCreateRequests(): Stream<Arguments> = Stream.of(
      Arguments.of("{}", "name", "Name is required"),
      Arguments.of("""{"name": ""}""", "name", "Name is required"),
      Arguments.of("""{"name": "Test"}""", "email", "Email is required"),
      Arguments.of("""{"name": "Test", "email": "invalid"}""", "email", "Email must be valid")
    )

    @JvmStatic
    fun invalidUpdateRequests(): Stream<Arguments> = Stream.of(
      Arguments.of("""{"email": "invalid"}""", "email", "Email must be valid")
    )
  }

  @Nested
  inner class CreateUser {

    @Test
    fun `should create user and return 201`() {
      val user = createTestUser()
      whenever(
        userUse.createUser(
          name = any(),
          email = any(),
          firstName = anyOrNull(),
          lastName = anyOrNull(),
          stravaId = anyOrNull(),
          avatarUrl = anyOrNull()
        )
      ).thenReturn(user)

      mockMvc.perform(
        post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
            {
              "name": "John Doe",
              "email": "john@example.com",
              "firstName": "John",
              "lastName": "Doe",
              "stravaId": 12345,
              "avatarUrl": "https://example.com/avatar.jpg"
            }
            """.trimIndent()
          )
      )
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.id").value(TEST_ID.toString()))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.stravaId").value(12345))
        .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.jpg"))
        .andExpect(jsonPath("$.isActive").value(true))
    }

    @Test
    fun `should create user with minimal fields`() {
      val user = createTestUser(firstName = null, lastName = null, stravaId = null, avatarUrl = null)
      whenever(
        userUse.createUser(
          name = any(),
          email = any(),
          firstName = anyOrNull(),
          lastName = anyOrNull(),
          stravaId = anyOrNull(),
          avatarUrl = anyOrNull()
        )
      ).thenReturn(user)

      mockMvc.perform(
        post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""{"name": "John Doe", "email": "john@example.com"}""")
      )
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
    }

    @ParameterizedTest
    @MethodSource("com.lab.strava.domain.user.UserControllerTest#invalidCreateRequests")
    fun `should return 400 for invalid create request`(
      requestBody: String,
      expectedField: String,
      expectedMessage: String
    ) {
      mockMvc.perform(
        post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody)
      )
        .andExpect(status().isBadRequest)
    }
  }

  @Nested
  inner class GetUserById {

    @Test
    fun `should return user when found`() {
      val user = createTestUser()
      whenever(userUse.getUserById(TEST_ID)).thenReturn(user)

      mockMvc.perform(get("/api/v1/users/$TEST_ID"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id").value(TEST_ID.toString()))
        .andExpect(jsonPath("$.name").value("John Doe"))
    }

    @Test
    fun `should return 404 when user not found`() {
      whenever(userUse.getUserById(TEST_ID)).thenThrow(EntityNotFoundException("User", TEST_ID))

      mockMvc.perform(get("/api/v1/users/$TEST_ID"))
        .andExpect(status().isNotFound)
        .andExpect(jsonPath("$.title").value("Entity Not Found"))
        .andExpect(jsonPath("$.entityType").value("User"))
    }
  }

  @Nested
  inner class GetAllUsers {

    @Test
    fun `should return empty list when no users`() {
      whenever(userUse.getAllUsers()).thenReturn(emptyList())

      mockMvc.perform(get("/api/v1/users"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$").isArray)
        .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `should return all users`() {
      val users = listOf(
        createTestUser(id = UUID.randomUUID(), name = "User 1", email = "user1@example.com"),
        createTestUser(id = UUID.randomUUID(), name = "User 2", email = "user2@example.com")
      )
      whenever(userUse.getAllUsers()).thenReturn(users)

      mockMvc.perform(get("/api/v1/users"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("User 1"))
        .andExpect(jsonPath("$[1].name").value("User 2"))
    }
  }

  @Nested
  inner class UpdateUser {

    @Test
    fun `should update user and return 200`() {
      val updatedUser = createTestUser(name = "Updated Name")
      whenever(
        userUse.updateUser(
          id = eq(TEST_ID),
          name = anyOrNull(),
          email = anyOrNull(),
          firstName = anyOrNull(),
          lastName = anyOrNull(),
          stravaId = anyOrNull(),
          avatarUrl = anyOrNull(),
          isActive = anyOrNull()
        )
      ).thenReturn(updatedUser)

      mockMvc.perform(
        put("/api/v1/users/$TEST_ID")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""{"name": "Updated Name"}""")
      )
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.name").value("Updated Name"))
    }

    @Test
    fun `should return 404 when updating non-existent user`() {
      whenever(
        userUse.updateUser(
          id = eq(TEST_ID),
          name = anyOrNull(),
          email = anyOrNull(),
          firstName = anyOrNull(),
          lastName = anyOrNull(),
          stravaId = anyOrNull(),
          avatarUrl = anyOrNull(),
          isActive = anyOrNull()
        )
      ).thenThrow(EntityNotFoundException("User", TEST_ID))

      mockMvc.perform(
        put("/api/v1/users/$TEST_ID")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""{"name": "Updated Name"}""")
      )
        .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @MethodSource("com.lab.strava.domain.user.UserControllerTest#invalidUpdateRequests")
    fun `should return 400 for invalid update request`(
      requestBody: String,
      expectedField: String,
      expectedMessage: String
    ) {
      mockMvc.perform(
        put("/api/v1/users/$TEST_ID")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody)
      )
        .andExpect(status().isBadRequest)
    }
  }

  @Nested
  inner class DeleteUser {

    @Test
    fun `should delete user and return 204`() {
      mockMvc.perform(delete("/api/v1/users/$TEST_ID"))
        .andExpect(status().isNoContent)

      verify(userUse).deleteUser(TEST_ID)
    }

    @Test
    fun `should return 404 when deleting non-existent user`() {
      whenever(userUse.deleteUser(TEST_ID)).thenThrow(EntityNotFoundException("User", TEST_ID))

      mockMvc.perform(delete("/api/v1/users/$TEST_ID"))
        .andExpect(status().isNotFound)
    }
  }
}
