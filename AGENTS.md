# AGENTS

This file provides guidance to agentic coding tools on how to build, test, and contribute to this repository.

## Development Process

1. Read the project README in `docs/README.md`
2. Review requirements from `docs/iterations/`
3. Work on iterations sequentially, one at a time
4. Read `iteration_N.md` and generate execution plan `plan_N.md` into `docs/plans/`
5. Commit changes iteratively

**Ask clarifying questions for every iteration before implementing.**

## Project Requirements

- Follow Spring Boot best practices
- Use hexagonal architecture
- Decouple shareable functions and common utilities
- Include unit tests with `@ParameterizedTest` when possible
- Generate example HTTP requests in `http/` folder using IntelliJ HTTP client format

## Architecture

### Root Package Hierarchy

```
com.lab.strava/
├── config/    # Spring configuration
├── domain/    # Domain features (business logic)
├── external/  # External system clients and integrations
└── utils/     # Shared utilities
```

### Feature Package Structure

Each feature in `domain/` follows this structure:

```
domain/{feature}/
├── dto/           # Data transfer objects
├── jpa/           # JPA entities and repositories
├── model/         # Domain models and adapter interfaces (NO Spring annotations)
├── service/       # Business logic implementation
├── validation/    # API and service layer validation
└── Controller.kt  # REST controller at package root
```

## Build Commands

```bash
./gradlew clean check    # Full clean + compile + tests
./gradlew run            # Run the application
./gradlew test           # Run tests only
./gradlew test --tests "com.lab.strava.SomeTestClass"  # Run single test class
./gradlew ktlintCheck    # Check code style
./gradlew ktlintFormat   # Auto-format code
```

## Repository and Workflow

- Branch naming: `iteration_{iterationNumber}`
- No force-push to shared branches; create PRs for code review
- Do NOT add "Co-Authored-By" lines to commit messages

## Code Style

### Formatting

- Use `.editorconfig` for formatting rules
- Avoid wildcard imports; group imports: stdlib, third-party, project-local
- Prefer Kotlin-style function/property formatting
- Use fluent code style with extension functions where possible

### Nullability

- Prefer non-nullable types; use `?` only when necessary
- Use `?.` and `?:` idiomatically
- Prefer data classes for simple immutable DTOs

### Naming Conventions

- Domain interfaces: use `Use` suffix (e.g., `BalanceUse`)
- Test names: backtick format (e.g., `` `should do this and that` ``)

### Error Handling

- Use common error format on the HTTP layer
- Translate errors to proper HTTP response codes
- Fail fast with clear exception messages for invalid inputs
- Prefer `sealed` or `Result` types for domain errors when it improves clarity
- Always close streams using `use` in Kotlin

### API Design

- Small methods with single responsibility
- Prefer object/DTO for methods with many parameters
- Use pure functions where possible (no hidden side-effects)

### Testing

- Primarily use unit tests
- Do not test data classes and DTOs
- Test controllers with `@WebMvcTest`
- Test edge cases and error handling

## References

- Project README: `docs/README.md`
- Iteration requirements: `docs/iterations/`
- Execution plans: `docs/plans/`
