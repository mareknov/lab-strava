# Iteration 01

- generate class hierarchy, interfaces, DTOs, JPAs to represent a `User` object
- generate also API layer to create and retrieve `User` object
- generate JPA to persist `User` object in Postgres database, database itself will come in next iteration
- generate unit and controller tests for `User` object

## Requirements

- every entity must have a unique identifier
- `User` object should include `id`, `name`, `email`
- every entity must have timestamps `createAt`, `updatedAt`
- suggest additional fields for `User` object if appropriate