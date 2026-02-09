--liquibase formatted sql

--changeset iteration-03:v002-create-activities-table
CREATE TABLE activities (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(50) NOT NULL,
  start_date TIMESTAMP WITH TIME ZONE NOT NULL,
  start_date_local TIMESTAMP WITH TIME ZONE NOT NULL,
  timezone VARCHAR(100) NOT NULL,
  distance NUMERIC(12,2) NOT NULL,
  elapsed_time INTEGER NOT NULL,
  moving_time INTEGER NOT NULL,
  description TEXT,
  total_elevation_gain NUMERIC(10,2),
  elev_high NUMERIC(10,2),
  elev_low NUMERIC(10,2),
  average_speed NUMERIC(8,4),
  max_speed NUMERIC(8,4),
  average_heartrate INTEGER,
  max_heartrate INTEGER,
  has_heartrate BOOLEAN NOT NULL DEFAULT false,
  average_cadence INTEGER,
  average_watts INTEGER,
  max_watts INTEGER,
  kilojoules NUMERIC(10,2),
  calories INTEGER,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_activities_type ON activities(type);
CREATE INDEX idx_activities_start_date ON activities(start_date);

--rollback DROP TABLE activities;
