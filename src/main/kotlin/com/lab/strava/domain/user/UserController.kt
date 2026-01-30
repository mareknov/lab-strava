package com.lab.strava.domain.user

import com.lab.strava.domain.user.dto.CreateUserRequest
import com.lab.strava.domain.user.dto.UpdateUserRequest
import com.lab.strava.domain.user.dto.UserResponse
import com.lab.strava.domain.user.model.UserUse
import com.lab.strava.domain.user.validation.UserValidation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v1/users")
class UserController(
  private val userUse: UserUse,
) {
  @PostMapping
  fun createUser(
    @Valid @RequestBody request: CreateUserRequest,
  ): ResponseEntity<UserResponse> {
    UserValidation.validateCreateRequest(request)
    val user =
      userUse.createUser(
        name = request.name,
        email = request.email,
        firstName = request.firstName,
        lastName = request.lastName,
        stravaId = request.stravaId,
        avatarUrl = request.avatarUrl,
      )
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user))
  }

  @GetMapping("/{id}")
  fun getUserById(
    @PathVariable id: UUID,
  ): ResponseEntity<UserResponse> {
    val user = userUse.getUserById(id)
    return ResponseEntity.ok(UserResponse.fromDomain(user))
  }

  @GetMapping
  fun getAllUsers(): ResponseEntity<List<UserResponse>> {
    val users = userUse.getAllUsers().map { UserResponse.fromDomain(it) }
    return ResponseEntity.ok(users)
  }

  @PutMapping("/{id}")
  fun updateUser(
    @PathVariable id: UUID,
    @Valid @RequestBody request: UpdateUserRequest,
  ): ResponseEntity<UserResponse> {
    UserValidation.validateUpdateRequest(request)
    val user =
      userUse.updateUser(
        id = id,
        name = request.name,
        email = request.email,
        firstName = request.firstName,
        lastName = request.lastName,
        stravaId = request.stravaId,
        avatarUrl = request.avatarUrl,
        isActive = request.isActive,
      )
    return ResponseEntity.ok(UserResponse.fromDomain(user))
  }

  @DeleteMapping("/{id}")
  fun deleteUser(
    @PathVariable id: UUID,
  ): ResponseEntity<Unit> {
    userUse.deleteUser(id)
    return ResponseEntity.noContent().build()
  }
}
