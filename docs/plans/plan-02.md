# Implementation Plan: Iteration 02 - PostgreSQL Migration with Liquibase

## Context

This iteration migrates the application from H2 in-memory database to PostgreSQL for local and dev profiles, while keeping H2 for tests. The migration introduces proper database version control using Liquibase with SQL format, providing rollback capability, context-based sample data loading, and migration tracking.

**Why this change:**
- Move from prototype (H2 with `create-drop`) to development-ready database setup
- Enable persistent data across application restarts
- Prepare foundation for production deployment
- Introduce migration versioning for team collaboration

**Tool choice:** Liquibase with SQL format
- Combines SQL simplicity with Liquibase features (contexts, rollbacks, tracking)
- Context support allows loading sample data only in `local` profile
- Rollback capability for development safety
- SQL transparency for easier code review and debugging

---

## Implementation Steps

### 1. Update Dependencies

**File:** `build.gradle.kts`

**Changes:**
```kotlin
dependencies {
  // ... existing dependencies ...

  // ADD: Liquibase for database migrations
  implementation("org.springframework.boot:spring-boot-starter-liquibase")

  // KEEP: PostgreSQL driver (already present)
  runtimeOnly("org.postgresql:postgresql")

  // CHANGE: Move H2 from runtimeOnly to testRuntimeOnly
  testRuntimeOnly("com.h2database:h2")  // Was: runtimeOnly("com.h2database:h2")

  // REMOVE: testImplementation("com.h2database:h2") - redundant with testRuntimeOnly
}
```

---

### 2. Create Docker Compose Setup

**File:** `docker-compose.yml` (new, at project root)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: lab-strava-postgres
    restart: unless-stopped
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: strava_db
      POSTGRES_USER: strava_user
      POSTGRES_PASSWORD: strava_pass
      PGDATA: /var/lib/postgresql/data/pgdata
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - strava-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U strava_user -d strava_db"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
    driver: local

networks:
  strava-network:
    driver: bridge
```

**Design notes:**
- Uses postgres:16-alpine for minimal footprint
- Named volume for data persistence
- Health check ensures database is ready before app connects
- Dev-only credentials (clearly not production-grade)

---

### 3. Create Liquibase Migration Structure

#### 3.1 Master Changelog

**File:** `src/main/resources/db/changelog/db.changelog-master.yaml` (new)

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/v001-create-users-table.sql
  - include:
      file: db/changelog/data/v001-sample-users.sql
      context: local
```

**Note:** Master file stays YAML (simple list), actual migrations are SQL.

#### 3.2 Users Table Migration

**File:** `src/main/resources/db/changelog/changes/v001-create-users-table.sql` (new)

```sql
--liquibase formatted sql

--changeset iteration-02:v001-create-users-table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    strava_id BIGINT UNIQUE,
    avatar_url VARCHAR(512),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

--rollback DROP TABLE users;
```

**Must match:** `domain/user/jpa/UserEntity.kt` field mappings exactly.

#### 3.3 Sample Data Migration

**File:** `src/main/resources/db/changelog/data/v001-sample-users.sql` (new)

```sql
--liquibase formatted sql

--changeset iteration-02:v001-insert-sample-users context:local
INSERT INTO users (id, name, email, first_name, last_name, strava_id, is_active, created_at, updated_at)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'John Doe', 'john.doe@example.com', 'John', 'Doe', 12345678, true, '2026-01-01T10:00:00Z', '2026-01-01T10:00:00Z'),
  ('22222222-2222-2222-2222-222222222222', 'Jane Smith', 'jane.smith@example.com', 'Jane', 'Smith', NULL, true, '2026-01-15T14:30:00Z', '2026-01-15T14:30:00Z');

--rollback DELETE FROM users WHERE id IN ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222');
```

**Context filtering:** `context:local` ensures this only runs in local profile, not dev/prod.

---

### 4. Update Configuration Files

#### 4.1 Local Profile (PostgreSQL)

**File:** `src/main/resources/application-local.yml`

**Replace entire file with:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lab_strava_db
    username: lab_strava_user
    password: lab_strava_pass
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    contexts: local
    enabled: true
```

**Key changes:**
- PostgreSQL datasource (was H2)
- `ddl-auto: validate` (was `create-drop`) - Liquibase owns schema now
- Liquibase enabled with `contexts: local` for sample data
- Removed H2 console config

#### 4.2 Dev Profile (PostgreSQL, no sample data)

**File:** `src/main/resources/application-dev.yml` (new)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/strava_db
    username: strava_user
    password: strava_pass
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    contexts: dev
    enabled: true
```

**Differences from local:**
- Larger connection pool (10 vs 5)
- `show-sql: false` for cleaner logs
- `contexts: dev` - no sample data loaded

#### 4.3 Test Profile (Keep H2)

**File:** `src/test/resources/application-test.yml`

**Add to existing config:**
```yaml
spring:
  # ... existing H2 config stays unchanged ...

  liquibase:
    enabled: false
```

**Why:** Tests use H2 with `ddl-auto: create-drop` for speed. No Liquibase needed.

---

### 5. Update Documentation

**File:** `CLAUDE.md`

**Add to Build Commands section:**
```markdown
## Database Setup

**Local/Dev:** PostgreSQL via Docker Compose
```bash
docker-compose up -d       # Start Postgres
docker-compose down        # Stop (keeps data)
docker-compose down -v     # Stop and remove data
docker logs lab-strava-postgres  # View logs
```

**Tests:** H2 in-memory (automatic, no setup needed)

**Migrations:** Liquibase with SQL format
- Master: `src/main/resources/db/changelog/db.changelog-master.yaml`
- Schema changes: `src/main/resources/db/changelog/changes/v*.sql`
- Sample data: `src/main/resources/db/changelog/data/v*.sql`

**Database Access:**
```bash
# Connect to Postgres
docker exec -it lab-strava-postgres psql -U strava_user -d strava_db

# Useful commands
\dt              # List tables
\d users         # Describe users table
\q               # Quit
```
```

---

## Critical Files Summary

| File | Action | Purpose |
|------|--------|---------|
| `build.gradle.kts` | EDIT | Add Liquibase, move H2 to test scope |
| `docker-compose.yml` | CREATE | PostgreSQL service definition |
| `src/main/resources/db/changelog/db.changelog-master.yaml` | CREATE | Master changelog (orchestrates migrations) |
| `src/main/resources/db/changelog/changes/v001-create-users-table.sql` | CREATE | Users table schema migration |
| `src/main/resources/db/changelog/data/v001-sample-users.sql` | CREATE | Sample data (local only) |
| `src/main/resources/application-local.yml` | REPLACE | PostgreSQL config for local |
| `src/main/resources/application-dev.yml` | CREATE | PostgreSQL config for dev |
| `src/test/resources/application-test.yml` | EDIT | Disable Liquibase for tests |
| `CLAUDE.md` | EDIT | Add database setup documentation |

---

## Verification Steps

### 1. Build & Test
```bash
./gradlew clean build
# All tests should pass (using H2)
```

### 2. Start Infrastructure
```bash
docker-compose up -d
docker ps  # Verify container running
```

### 3. Run Application (Local Profile)
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

**Expected logs:**
```
Successfully acquired change log lock
Running Changeset: db/changelog/changes/v001-create-users-table.sql
ChangeSet db/changelog/changes/v001-create-users-table.sql ran successfully
Running Changeset: db/changelog/data/v001-sample-users.sql (context: local)
ChangeSet db/changelog/data/v001-sample-users.sql ran successfully
```

### 4. Verify Database Schema
```bash
docker exec -it lab-strava-postgres psql -U strava_user -d strava_db
```
```sql
\dt  -- Should show: users, databasechangelog, databasechangeloglock
\d users  -- Verify columns match UserEntity
SELECT * FROM users;  -- Should show 2 sample users
\q
```

### 5. Test API Endpoints
```http
GET http://localhost:8080/api/v1/users
# Should return 2 sample users
```

### 6. Run Application (Dev Profile)
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Verify:**
- App starts successfully
- Migrations run
- Query users table - should be empty (no sample data in dev context)

### 7. Test Application Restart
```bash
# Stop app (Ctrl+C)
# Start again
./gradlew bootRun --args='--spring.profiles.active=local'

# Liquibase should skip already-run changesets (idempotent)
```

### 8. Verify Data Persistence
```bash
docker-compose down
docker-compose up -d
./gradlew bootRun --args='--spring.profiles.active=local'

# Data should still exist (persisted in volume)
```

---

## Success Criteria

- ✅ Docker Compose starts PostgreSQL successfully
- ✅ Application connects to PostgreSQL in local profile
- ✅ Liquibase migrations run on first startup
- ✅ Users table created with correct schema
- ✅ 2 sample users loaded in local profile
- ✅ No sample users in dev profile
- ✅ All existing tests pass (using H2)
- ✅ API endpoints work with PostgreSQL data
- ✅ Liquibase tracking tables exist (databasechangelog, databasechangeloglock)
- ✅ Data persists across container restarts
- ✅ Migrations are idempotent (safe to restart app)

---

## Rollback Plan

If critical issues arise:

1. Revert `build.gradle.kts`: Restore H2 to `runtimeOnly`, remove Liquibase
2. Revert `application-local.yml`: Restore H2 configuration from git history
3. Delete Liquibase files: Remove `db/changelog/` directory
4. Stop Docker: `docker-compose down -v`
5. Verify tests pass: `./gradlew test`

The test suite running on H2 ensures we always have a working baseline.

---

## Future Enhancements (Not in This Iteration)

- Testcontainers: Real PostgreSQL in integration tests
- pgAdmin service in docker-compose.yml
- Production profile with external PostgreSQL
- Liquibase Gradle plugin for rollback commands
- Migration testing in CI/CD pipeline
