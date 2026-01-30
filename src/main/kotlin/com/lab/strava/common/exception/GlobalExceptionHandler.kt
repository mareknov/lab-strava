package com.lab.strava.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
  @ExceptionHandler(EntityNotFoundException::class)
  fun handleEntityNotFound(ex: EntityNotFoundException): ProblemDetail =
    ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Entity not found").apply {
      title = "Entity Not Found"
      setProperty("entityType", ex.entityType)
      setProperty("entityId", ex.entityId.toString())
    }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
    val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed").apply {
      title = "Validation Error"
      setProperty("errors", errors)
    }
  }

  @ExceptionHandler(IllegalArgumentException::class)
  fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail =
    ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid argument").apply {
      title = "Bad Request"
    }
}
