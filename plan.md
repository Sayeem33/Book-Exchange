# Book Exchange Platform - Phase-Wise Execution Plan

This plan satisfies all project requirements, but testing is done within each phase, not all at once.
From Day 1, every push/PR is automatically validated in GitHub Actions before merge.

## 1. Core Objective

Build a complete Book Exchange web application using:
- Spring Boot, Thymeleaf, Spring Security, PostgreSQL, JPA
- Docker and docker compose
- GitHub workflow with protected `main`
- CI checks before merge
- Deployment to Render from `main`

## 2. Team and Git Workflow (Start First)

### 2.1 Branch Model
- `main` (protected, production)
- `develop` (integration branch)
- `feature/*` (all implementation work)

### 2.2 Merge Flow
1. Create `feature/*` branch from `develop`.
2. Implement one phase scope.
3. Run local tests for that phase.
4. Push and open PR to `develop`.
5. GitHub Actions runs checks automatically.
6. Merge only when checks pass and review is approved.
7. Release via PR from `develop` to `main`.
8. `main` merge triggers deployment to Render.

### 2.3 Branch Protection Rules (`main` and recommended for `develop`)
- Require PR before merge.
- Require at least 1 approval.
- Require status checks to pass.
- Block direct push and force push.
- Optional: require branch up-to-date before merge.

## 3. CI/CD Foundation (Enable Early, Then Keep Using)

Set up CI/CD early so each later phase is protected automatically.

### 3.1 CI Workflow (`.github/workflows/ci.yml`)
Triggers:
- `pull_request` on `develop`, `main`
- `push` on `develop`, `main`

Jobs:
1. `build-and-test`
- Java 17 setup
- Maven cache
- `./mvnw -B clean verify`

2. `docker-build` (recommended)
- Build Docker image to ensure Dockerfile is healthy

### 3.2 CD Workflow (`.github/workflows/deploy-render.yml`)
- Trigger: push to `main`
- Only deploy if CI checks passed
- Deploy using Render deploy hook or API token

### 3.3 Required Checks for Merge
Set required checks in GitHub repo settings:
- `build-and-test`
- `docker-build` (if enabled)

Outcome: after each push/PR, GitHub auto-tests code and blocks bad merges.

## 4. Implementation Plan by Phase (Testing in Every Phase)

## Phase 1 - Project Skeleton, DB Config, Docker Baseline

### Build Tasks
- Create package structure:
  - `controller`, `service`, `repository`, `entity`, `dto`, `exception`, `security`, `config`
- Configure profiles (`dev`, `test`, `prod`) in `application.yaml`.
- Add DB configs via environment variables (no hardcoded credentials).
- Add `Dockerfile` (multi-stage build).
- Upgrade `compose.yaml`:
  - `postgres` with fixed version
  - `app` service
  - env vars and volume
  - healthchecks

### Testing in This Phase
- Smoke test app startup locally.
- Run `mvnw.cmd test` (or `./mvnw test` on Unix).
- Run `docker compose up --build` to validate container startup.

### Merge Gate for This Phase
- PR cannot merge unless CI is green.

## Phase 2 - Security and User Management

### Build Tasks
- Implement user registration/login/logout.
- Implement BCrypt password encoding.
- Implement roles: `ADMIN`, `SELLER`, `BUYER`.
- Configure URL/method-level authorization.
- Add base user-role entities and repositories.

### Testing in This Phase
- Unit tests for registration and role assignment logic.
- Integration tests for auth and role-based restrictions (`401/403` checks).
- Validate unauthorized/forbidden access paths.

### Merge Gate for This Phase
- Unit and integration tests for security must pass in CI.

## Phase 3 - Domain Model and Core Business Logic

### Build Tasks
- Implement required entities (minimum 4 tables):
  - `users`, `roles`, `books`, `exchange_requests`
- Define proper relationships (1:M, M:M where needed).
- Implement repositories and service layer logic.
- Add DTOs and mappers.

### Testing in This Phase
- Unit tests for `BookService` and `ExchangeRequestService` business rules.
- Validate ownership checks and status transitions.
- Ensure DB mapping works under test profile.

### Merge Gate for This Phase
- Service tests must pass; no merge on failing business logic tests.

## Phase 4 - REST API Controllers and Exception Handling

### Build Tasks
- Implement minimum 3 controllers:
  - `AuthController`
  - `BookController`
  - `ExchangeRequestController`
- Provide CRUD for at least 2 entities.
- Add request validation (`@Valid`).
- Add global exception handling (`@ControllerAdvice`).

### Testing in This Phase
- Integration tests using `MockMvc` for key endpoints and status codes.
- Validate proper HTTP methods and responses (`200`, `201`, `204`, `400`, `401`, `403`, `404`, `409`).

### Merge Gate for This Phase
- Controller integration tests must pass in CI.

## Phase 5 - Thymeleaf UI and Role-Based Screens

### Build Tasks
- Create minimal pages:
  - home listing
  - login/register
  - seller dashboard
  - buyer request page
  - admin page
- Connect MVC controllers and templates.
- Ensure role-based visibility/actions in UI flow.

### Testing in This Phase
- Integration tests for key MVC routes and role access.
- Manual end-to-end checks for major user flows.

### Merge Gate for This Phase
- MVC tests and existing API tests must stay green.

## Phase 6 - Test Completion Target

### Build Tasks
- Complete and stabilize test suite to required count:
  - at least 15 unit tests
  - at least 3 integration tests
- Refactor flaky tests and improve readability.

### Testing in This Phase
- Run full `clean verify` locally and in CI.
- Confirm consistent green builds on multiple PRs.

### Merge Gate for This Phase
- Keep `build-and-test` mandatory for all merges.

## Phase 7 - Deployment to Render

### Build Tasks
- Provision Render web service + PostgreSQL.
- Add Render env vars (`SPRING_PROFILES_ACTIVE=prod`, DB vars).
- Connect deployment workflow from `main`.

### Testing in This Phase
- Post-deploy smoke checks:
  - app URL reachable
  - login works
  - core flows work
- Check logs for startup/runtime errors.

### Merge Gate for This Phase
- Deploy only from merged and CI-passed `main` commits.

## Phase 8 - Documentation and Final Submission

### Build Tasks
Create `README.md` with:
- Project description and architecture overview
- Architecture diagram and ER diagram
- Tech stack
- Local run instructions (Maven + Docker)
- API endpoint summary
- Role matrix and security model
- Testing strategy and test counts
- CI/CD explanation and branch rules
- Render live URL and GitHub repo link

### Testing in This Phase
- Validate all README commands work.
- Perform final full regression run.

### Merge Gate for This Phase
- Docs update PR must also pass CI.

## 5. Requirement-to-Plan Mapping

- Authentication + authorization: Phase 2
- REST design + 3 controllers + CRUD: Phase 4
- Database with 4+ tables + relationships: Phase 3
- Testing requirement (15 unit, 3 integration): Phase 6 (built incrementally from Phases 2-5)
- Dockerization: Phase 1
- GitHub requirements (branch strategy + PR approvals): Section 2
- CI/CD pipeline: Section 3 and applied in every phase
- Deployment on Render: Phase 7
- Required documentation: Phase 8

## 6. Final Compliance Checklist

### Mandatory Functional Checklist
- [ ] Spring Security auth implemented
- [ ] BCrypt password encryption active
- [ ] Role-based access for `ADMIN`, `SELLER`, `BUYER`
- [ ] Minimum 3 controllers completed
- [ ] CRUD for at least 2 entities completed
- [ ] Global exception handling implemented
- [ ] PostgreSQL schema with minimum 4 tables
- [ ] Proper entity relationships implemented
- [ ] At least 15 unit tests
- [ ] At least 3 integration tests
- [ ] Dockerfile completed
- [ ] `compose.yaml` runs app + postgres
- [ ] `docker compose up --build` works
- [ ] Branch strategy enforced
- [ ] PR review policy enabled
- [ ] GitHub Actions checks required before merge
- [ ] Auto deployment to Render from `main`
- [ ] Public deployed URL working
- [ ] README complete with all required sections

### Automatic Failure Prevention Checklist
- [ ] Role-based access enforced
- [ ] No direct push to `main`
- [ ] Dockerization complete
- [ ] Tests implemented and executed in CI
- [ ] App deployed and accessible

## 7. Immediate Action Order

1. Configure branch protections and required GitHub checks.
2. Add CI workflow (`build-and-test`) and verify PR gating.
3. Implement Phase 1 and test it before merge.
4. Continue phase-by-phase implementation with matching tests.
5. Keep every PR small, reviewed, and CI-passing.
