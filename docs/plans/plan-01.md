# Iteration 01: User Domain Implementation Plan

## Overview

Implement the User domain following hexagonal architecture with full CRUD API, JPA persistence, and tests.

## Dependencies to Add

```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-validation")
runtimeOnly("org.postgresql:postgresql")
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("com.h2database:h2")
testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
```

## User Entity Fields

**Required fields:**
- `id` (UUID) - unique identifier
- `name` (String) - display name
- `email` (String) - unique email
- `createdAt` (Instant) - creation timestamp
- `updatedAt` (Instant) - last update timestamp

**Additional fields (confirmed):**
- `firstName`, `lastName` (String?) - separate name components
- `stravaId` (Long?) - Strava API user ID for future integration
- `avatarUrl` (String?) - profile picture URL
- `isActive` (Boolean) - soft delete support, defaults to true

## Package Structure

```
com.lab.strava/
├── common/exception/
│   ├── EntityNotFoundException.kt
│   └── GlobalExceptionHandler.kt
└── domain/user/
    ├── UserController.kt
    ├── dto/
    │   ├── CreateUserRequest.kt
    │   ├── UpdateUserRequest.kt
    │   └── UserResponse.kt
    ├── jpa/
    │   ├── UserEntity.kt
    │   └── UserRepository.kt
    ├── model/
    │   ├── User.kt          # Domain model (NO Spring annotations)
    │   └── UserUse.kt       # Service interface
    ├── service/
    │   └── UserService.kt
    └── validation/
        └── UserValidation.kt
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/users` | Create user |
| GET | `/api/v1/users/{id}` | Get user by ID |
| GET | `/api/v1/users` | Get all users |
| PUT | `/api/v1/users/{id}` | Update user |
| POST | `/api/v1/users/{id}/deactivate` | Deactivate (soft delete) user |

## Implementation Order

1. **Dependencies & Config** - Update build.gradle.kts, create application.yml
2. **Common Exception Handling** - EntityNotFoundException, GlobalExceptionHandler
3. **Domain Model** - User.kt (pure Kotlin), UserUse.kt interface
4. **JPA Layer** - UserEntity.kt, UserRepository.kt
5. **DTOs** - CreateUserRequest, UpdateUserRequest, UserResponse
6. **Service** - UserService implementing UserUse
7. **Validation** - UserValidation.kt
8. **Controller** - UserController.kt
9. **Tests** - UserTest, UserServiceTest, UserControllerTest
10. **HTTP Files** - user-api.http with example requests

## Test Strategy

- **Unit tests**: User domain model, UserService with mocked repository
- **Controller tests**: @WebMvcTest with mocked UserUse
- **Parametrized tests**: Multiple input scenarios using @ParameterizedTest

## Configuration Files

- `src/main/resources/application.yml` - PostgreSQL config (for iteration 02)
- `src/test/resources/application-test.yml` - H2 in-memory for tests
- `http/user-api.http` - IntelliJ HTTP client requests

## Verification

1. Run `./gradlew clean check` - all tests pass
2. Run `./gradlew run` - application starts (will fail DB connection until iteration 02)
3. Review test coverage for User domain
