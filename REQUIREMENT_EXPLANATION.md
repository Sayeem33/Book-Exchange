# Mandatory Requirement Explanation Report

Project: Book-Exchange  
Branch reviewed: feature/phase6-testing  
Date: 2026-04-04

This report explains each mandatory requirement and whether it is fulfilled in the current workspace snapshot.

## Summary

1. Authentication & Authorization: Fulfilled  
2. REST API Design: Not Fulfilled  
3. Database: Partially Fulfilled  
4. Testing: Not Fulfilled  
5. Dockerization: Fulfilled  
6. GitHub Requirements: Partially Fulfilled / Not Verifiable from code only  
7. CI/CD Pipeline: Not Fulfilled in current snapshot  
8. Deployment: Not Fulfilled / Not Verifiable

---

## 1) Authentication & Authorization

Status: **Fulfilled**

What is required:
- Spring Security
- User registration
- Login/logout
- BCrypt password encryption
- Role-based authorization (ADMIN, SELLER, BUYER)
- Access restriction by method-level or URL-based security

What exists now:
- Spring Security configuration is present and active.
- URL-based role restrictions are configured for admin, seller, and buyer route groups.
- Registration and login pages/controllers are implemented.
- Logout is configured in SecurityConfig.
- BCrypt encoder bean is configured and password is encoded before saving users.
- Roles ADMIN, SELLER, BUYER are defined.

Conclusion:
- Requirement is satisfied using URL-based authorization (method-level is optional because requirement allows either).

---

## 2) REST API Design

Status: **Not Fulfilled**

What is required:
- REST principles with proper HTTP methods and status codes
- Global exception handling
- Minimum 3 controllers
- CRUD for at least 2 main entities

What exists now:
- Controllers are mainly MVC/Thymeleaf controllers returning view names, not REST-style JSON APIs.
- Some endpoints use GET/POST, but PUT/DELETE style CRUD endpoints are missing for main entities.
- Global exception handling class is not present in current source snapshot.
- Proper REST response management (ResponseEntity + explicit status mapping) is not consistently implemented.

Conclusion:
- Requirement is currently not met.

---

## 3) Database

Status: **Partially Fulfilled**

What is required:
- PostgreSQL
- At least 4 tables
- Proper relationships (1:M, M:1, M:M)
- JPA usage

What exists now:
- PostgreSQL datasource configuration is present.
- Docker compose includes PostgreSQL service.
- JPA entities and repositories are implemented.
- Relationship coverage exists:
  - 1:M between User and Book
  - M:1 in Book to User and SavedBook to User/Book
  - M:M behavior is represented through SavedBook join-entity pattern

Gap:
- Current model appears to have 3 core tables (users, book, saved_book). Role is enum-based, not a separate table.
- Requirement asks at least 4 tables.

Conclusion:
- Partially fulfilled; needs one additional table/entity (or convert role enum to role table with relationship).

---

## 4) Testing

Status: **Not Fulfilled**

What is required:
- Unit tests (service layer) using JUnit + Mockito
- Integration tests (controller layer) using SpringBootTest + MockMvc
- Minimum 15 unit tests and 3 integration tests
- Tests run successfully in CI

What exists now:
- Service-layer unit tests are present and use JUnit/Mockito.
- Current service-layer unit test count is 12.
- Controller tests currently use standalone MockMvc setup, which are slice/standalone tests, not full Spring Boot integration tests.
- Only one SpringBootTest class is present in current snapshot.
- CI workflows are missing in the current workspace snapshot, so CI test execution cannot be proven.

Local execution note:
- Test run failed locally in this environment because JAVA_HOME is not configured correctly, so live pass/fail confirmation was blocked.

Conclusion:
- Requirement is not met yet (unit test minimum and integration test style/quantity gap).

---

## 5) Dockerization

Status: **Fulfilled**

What is required:
- Dockerfile
- docker-compose with app + PostgreSQL containers
- Environment variables (no hardcoded credentials)
- Must run with docker compose up --build

What exists now:
- Dockerfile exists and builds application image.
- compose.yaml includes app service and PostgreSQL service.
- Environment variables are used for DB credentials and app configuration.
- docker compose up --build was executed successfully in this session.

Conclusion:
- Requirement is satisfied.

---

## 6) GitHub Requirements

Status: **Partially Fulfilled / Not Verifiable from code only**

What is required:
- Branch strategy: main (protected), develop, and feature branches
- No direct push to main
- PR required with at least one review approval

What is verifiable now:
- Current work is on a feature branch.
- Repository has main as default branch (from context attachment).

What is not verifiable from local source files:
- Whether main is protected
- Whether direct pushes are blocked
- Whether PR approval rules are enforced
- Whether develop branch policy is actively configured

Conclusion:
- This requirement depends on GitHub repository settings and workflow policy, not only local code.
- Mark as partially fulfilled/not verifiable until validated in GitHub settings.

---

## 7) CI/CD Pipeline

Status: **Not Fulfilled in current snapshot**

What is required:
- GitHub Actions pipeline to build project, run tests, and deploy to Render automatically from main

What exists now:
- In the current workspace snapshot, .github/workflows files are not present.
- Therefore build/test/deploy pipeline cannot be verified from current code.

Conclusion:
- Requirement is currently not fulfilled in this snapshot.

---

## 8) Deployment

Status: **Not Fulfilled / Not Verifiable**

What is required:
- Deploy on Render
- Publicly accessible application
- Submit live URL and GitHub repository link

What exists now:
- No deploy workflow/config evidence in current workspace snapshot.
- No live Render URL is present in current project files.
- GitHub repository link is available from repository context, but live URL evidence is missing.

Conclusion:
- Requirement is not yet fully satisfied until deployment is completed and live URL is provided.

---

## Final Recommendation

To move this report to all-fulfilled status, prioritize these fixes in order:

1. Implement real REST controllers with full CRUD (GET/POST/PUT/DELETE) for at least 2 entities and add global exception handler.
2. Add at least one more database table/entity and keep proper relationships.
3. Increase service-layer unit tests from 12 to at least 15.
4. Add at least 3 real controller integration tests using SpringBootTest + MockMvc.
5. Add .github/workflows CI and deploy-render pipeline files.
6. Configure GitHub branch protection and PR review policy in repo settings.
7. Complete Render deployment and include live URL in submission docs.

