--liquibase formatted sql

--changeset iteration-02:v001-insert-sample-users context:local
INSERT INTO users (id, name, email, first_name, last_name, strava_id, is_active, created_at, updated_at)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'John Doe', 'john.doe@example.com', 'John', 'Doe', 12345678, true, '2026-01-01T10:00:00Z', '2026-01-01T10:00:00Z'),
  ('22222222-2222-2222-2222-222222222222', 'Jane Smith', 'jane.smith@example.com', 'Jane', 'Smith', NULL, true, '2026-01-15T14:30:00Z', '2026-01-15T14:30:00Z');

--rollback DELETE FROM users WHERE id IN ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222');
