package com.lab.strava.common.exception

class EntityNotFoundException(
  val entityType: String,
  val entityId: Any,
) : RuntimeException("$entityType with id '$entityId' not found")
