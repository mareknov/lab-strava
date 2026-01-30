# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Kotlin/Spring Boot REST API for Strava activity management with RAG and LLM integration. The app loads activities from Strava's public API, stores them in PostgreSQL and a vector database, and provides LLM-powered question answering.

## Build Commands

```bash
./gradlew clean check    # Full clean + compile + tests
./gradlew run            # Run the application
./gradlew test           # Run tests only
./gradlew test --tests "com.lab.strava.SomeTestClass"  # Run single test class
./gradlew ktlintCheck    # Check code style
./gradlew ktlintFormat   # Auto-format code
```

## Architecture

**Hexagonal architecture** with strict package organization under `com.lab.strava`:

### Root Packages
- `config`   - Spring configuration
- `domain`   - Domain features (business logic)
- `external` - External system clients and integrations
- `utils`    - Shared utilities

### Feature Package Structure
Each feature in `domain` follows this structure:
```
domain/{feature}/
├── dto/           # Data transfer objects
├── jpa/           # JPA entities and repositories
├── model/         # Domain models and adapter interfaces (NO Spring annotations)
├── service/       # Business logic implementation
├── validation/    # API and service layer validation
└── Controller.kt  # REST controller at package root
```

## Code Conventions

- **Formatting**: 2-space indents, 120-char max line length (see `.editorconfig`)
- **Imports**: No wildcard imports; group by stdlib, third-party, project-local
- **Nullability**: Prefer non-nullable types; use `?` only when necessary
- **Domain interfaces**: Use `Use` suffix (e.g., `BalanceUse`)
- **Test names**: Backtick format: `` `should do this and that` ``
- **Testing**: Use `@ParameterizedTest` when possible; `@WebMvcTest` for controllers
- **HTTP examples**: Add IntelliJ HTTP client requests in `http/` folder

## Git Commits

- Do NOT add "Co-Authored-By" lines to commit messages
- Use conventional commit format (e.g., `feat(scope): message`, `docs: message`)

## Development Process

1. Read iteration requirements from `docs/iterations/iteration_N.md`
2. Generate execution plan in `docs/plans/plan_N.md`
3. Ask clarifying questions before implementing
4. Work on iterations sequentially
5. Branch naming: `iteration_{iterationNumber}`

## Key Files

- `AGENT.md` - Detailed development guidelines
- `docs/README.md` - Project vision
- `docs/iterations/` - Iteration requirements
- `docs/plans/` - Execution plans
