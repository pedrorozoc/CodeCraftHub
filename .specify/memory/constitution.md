<!--
Sync Impact Report
- Version change: template/unversioned → 1.0.0
- Modified principles: none; initial constitution established.
- Added sections: Core Principles, Technical Constraints, Development Workflow, Governance.
- Removed sections: none.
- Follow-up TODOs: none.
-->
# CodeCraftHub Constitution

## Core Principles

### I. Beginner-Centered Simplicity
CodeCraftHub MUST favor small, readable components and direct control flow over abstractions
that do not teach a REST API fundamental. Each feature MUST have a clear responsibility and
documentation appropriate for a developer learning Spring Boot. New dependencies, layers, or
patterns require a concrete learning or maintenance benefit. This keeps the application
approachable and makes its API behavior easy to trace.

### II. Spring Boot REST First
The backend MUST be implemented in Java with Spring Boot and expose course capabilities through
HTTP REST endpoints. Controllers MUST use resource-oriented routes, standard HTTP methods and
status codes, validate request input, and return consistent JSON representations. API examples
and tests MUST cover successful and invalid requests. The API is the primary learning artifact,
so its behavior must be explicit and predictable.

### III. File-Based JSON Is the System of Record
Course data MUST be persisted to simple JSON files; no database, ORM, or external persistence
service is permitted. A dedicated persistence boundary MUST load data safely at startup and
write changes atomically enough to avoid malformed files under normal operation. Missing or
invalid files MUST result in actionable errors or an intentionally initialized empty collection,
never silent data loss. This preserves the intended focus on basic REST concepts and transparent
storage.

### IV. Course Model Integrity
Every course MUST contain a non-blank name, a description, a target completion date, and exactly
one current status: `NO_INICIADO`, `EN_PROGRESO`, or `COMPLETADO`. Requests and persisted data
MUST be validated against these constraints before mutation. Any future representation intended
for human display MAY translate the status labels but MUST retain an unambiguous machine value.
Consistent data makes progress tracking reliable.

### V. Scope and Privacy by Default
The application MUST manage learning courses only. It MUST NOT include authentication, accounts,
roles, permissions, or user administration unless this constitution is amended. Features outside
course tracking require a documented learning objective and a justified scope change. Keeping a
single-user, unauthenticated scope limits incidental complexity.

## Technical Constraints

- Java and Spring Boot are mandatory for application code.
- JSON files stored locally are the sole persistent source of course data.
- The baseline API MUST support creating, listing, retrieving, updating, and deleting courses,
  as well as updating a course's current status when that behavior is exposed separately.
- Course endpoints MUST consistently use JSON request and response bodies except for responses
  where HTTP semantics require no body.
- Database migrations, authentication flows, and user-management endpoints are out of scope.

## Development Workflow

Each change MUST begin with a written specification of the learner-facing behavior before
implementation. Work MUST include focused tests for validation, HTTP status behavior, and JSON
file read/write behavior affected by the change. Code review or self-review MUST verify the
course model, REST conventions, JSON persistence, and scope constraints. If a change increases
complexity, its rationale and simpler alternatives MUST be documented in the plan or pull request.

## Governance

This constitution supersedes conflicting project practices. Amendments MUST document the proposed
rule change, its rationale, its compatibility impact, and any required migration of JSON files or
API consumers. The version follows semantic versioning: MAJOR for incompatible governance changes,
MINOR for new or materially expanded principles or sections, and PATCH for clarifications that do
not change obligations. Every specification, plan, implementation review, and release review MUST
check compliance; exceptions require an explicit, time-bounded amendment or recorded waiver.

**Version**: 1.0.0 | **Ratified**: 2026-11-01 | **Last Amended**: 2026-11-01
