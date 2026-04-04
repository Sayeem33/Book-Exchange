# Marking Criteria Explanation (Up To Date)

Project: Book-Exchange  
Branch: feature/phase6-testing  
Date: 2026-04-04

This file explains each rubric point in 4 parts:
1. What it is
2. Where it is in the current code
3. How it works now
4. Current status

---

## A) Architecture & Code Quality (20)

### 1) Layered architecture (5)

What it is:
A layered structure separates responsibilities into controller, service, repository, and entity layers so code is easier to maintain and test.

Where in your code:
- Controllers: src/main/java/com/example/Book_Exchange/controller
- Services: src/main/java/com/example/Book_Exchange/service
- Repositories: src/main/java/com/example/Book_Exchange/repository
- Entities: src/main/java/com/example/Book_Exchange/entity

How it works now:
Request flow is controller -> service -> repository -> database. The codebase follows this pattern consistently for auth, seller, buyer, and admin flows.

Current status:
Fulfilled

### 2) Clean code & naming (5)

What it is:
Readable classes/methods, meaningful names, and clear separation of responsibilities.

Where in your code:
- src/main/java/com/example/Book_Exchange/service/BookService.java
- src/main/java/com/example/Book_Exchange/service/UserService.java
- src/main/java/com/example/Book_Exchange/service/SavedBookService.java
- src/main/java/com/example/Book_Exchange/controller/*.java

How it works now:
Names are understandable and business flow is easy to follow. Services hold core logic while controllers are relatively thin.

Current status:
Fulfilled

### 3) DTO usage (5)

What it is:
DTOs isolate API request/response payloads from entities.

Where in your code:
No dto package exists in current snapshot.

How it works now:
Entities are used directly in controller/service flow instead of dedicated DTO request/response models.

Current status:
Not Fulfilled

### 4) Exception handling (5)

What it is:
Centralized exception handling (usually via ControllerAdvice) to return consistent error responses and status codes.

Where in your code:
No global exception handler class found in current snapshot.

How it works now:
Some runtime exceptions are thrown directly (for example in service layer), but there is no centralized mapping for clean API error responses.

Current status:
Not Fulfilled

---

## B) Security & Role Management (15)

### 1) Spring Security implemented (5)

What it is:
Security framework for authentication and authorization.

Where in your code:
- src/main/java/com/example/Book_Exchange/config/SecurityConfig.java
- src/main/java/com/example/Book_Exchange/service/CustomUserDetailsService.java

How it works now:
SecurityFilterChain is configured, login page and logout are configured, and user details are loaded from database.

Current status:
Fulfilled

### 2) Password encryption (3)

What it is:
Store passwords securely using BCrypt hash.

Where in your code:
- src/main/java/com/example/Book_Exchange/config/SecurityConfig.java
- src/main/java/com/example/Book_Exchange/service/UserService.java

How it works now:
BCryptPasswordEncoder bean is configured and user passwords are encoded during registration before saving.

Current status:
Fulfilled

### 3) Role-based access enforced (7)

What it is:
Restrict access by user role (ADMIN, SELLER, BUYER).

Where in your code:
- src/main/java/com/example/Book_Exchange/config/SecurityConfig.java
- src/main/java/com/example/Book_Exchange/entity/Role.java

How it works now:
URL-based authorization is implemented:
- /admin/** -> ADMIN
- /seller/** -> SELLER
- /buyer/** -> BUYER
Other routes require authentication.

Current status:
Fulfilled

---

## C) Testing (15)

### 1) Unit tests quality (7)

What it is:
Service-layer tests using JUnit + Mockito with meaningful positive and negative scenarios.

Where in your code:
- src/test/java/com/example/Book_Exchange/service/BookServiceTest.java
- src/test/java/com/example/Book_Exchange/service/UserServiceTest.java
- src/test/java/com/example/Book_Exchange/service/SavedBookServiceTest.java
- src/test/java/com/example/Book_Exchange/service/CustomUserDetailsServiceTest.java

How it works now:
Service tests use Mockito and cover major service methods.
Current service test method count is 12, while rubric requirement says minimum 15 unit tests.

Current status:
Partially Fulfilled

### 2) Integration tests (5)

What it is:
Controller-level integration tests using SpringBootTest and MockMvc.

Where in your code:
- src/test/java/com/example/Book_Exchange/controller/*.java
- src/test/java/com/example/Book_Exchange/BookExchangeApplicationTests.java

How it works now:
Current controller tests are standalone MockMvc tests, not full SpringBootTest integration tests. Only one class uses SpringBootTest (contextLoads test).

Current status:
Not Fulfilled

### 3) Tests run in CI (3)

What it is:
Automated test execution in GitHub Actions on push/PR.

Where in your code:
No .github/workflows files are present in current snapshot.

How it works now:
Cannot verify CI execution from repository files currently available.

Current status:
Not Fulfilled

---

## D) Dockerization (10)

### 1) Proper Dockerfile (4)

What it is:
Container build for Spring Boot app.

Where in your code:
- Dockerfile

How it works now:
Multi-stage Docker build is configured (builder stage + runtime stage), and app runs as jar.

Current status:
Fulfilled

### 2) docker-compose works (4)

What it is:
Compose setup to run app + PostgreSQL together.

Where in your code:
- compose.yaml

How it works now:
compose has postgres service and app service with dependency/healthcheck linkage.
Latest session command docker compose up --build -d exited with code 0.

Current status:
Fulfilled

### 3) Environment config handled correctly (2)

What it is:
Credentials/config through environment variables, not hardcoded.

Where in your code:
- src/main/resources/application.yaml
- compose.yaml
- .env.example

How it works now:
Datasource URL/user/password and app port are environment-driven with defaults.

Current status:
Fulfilled

---

## E) CI/CD & Git Workflow (15)

### 1) Branch protection configured (5)

What it is:
Protected main branch and policy enforcement in GitHub settings.

Where in your code:
Not stored in source code; configured on GitHub repository settings.

How it works now:
Cannot be fully verified from local workspace only. Current branch is feature/phase6-testing and default branch is main (from repo context), but protection rules are not visible here.

Current status:
Not Verifiable from code snapshot

### 2) GitHub Actions workflow correct (5)

What it is:
Workflow file that builds and tests project automatically.

Where in your code:
No workflow files found in .github/workflows in current snapshot.

How it works now:
Cannot confirm workflow logic because files are missing in current local tree.

Current status:
Not Fulfilled

### 3) Automatic deployment working (5)

What it is:
Auto-deploy from main branch to hosting platform (Render).

Where in your code:
No deploy workflow file found in current snapshot.

How it works now:
No local evidence that auto deployment is currently wired.

Current status:
Not Fulfilled

---

## F) Database Design (10)

### Proper entity relationships (10)

What it is:
Correct table modeling and JPA relationships aligned with domain.

Where in your code:
- src/main/java/com/example/Book_Exchange/entity/User.java
- src/main/java/com/example/Book_Exchange/entity/Book.java
- src/main/java/com/example/Book_Exchange/entity/SavedBook.java

How it works now:
- One user (seller) to many books is implemented.
- SavedBook maps buyer-book relationship and prevents duplicate saves at service level.
- Core relationship mapping is valid for current features.

Current status:
Fulfilled for relationship quality in current model

Note:
If assessor also expects minimum table count from mandatory requirements, current schema appears to have 3 main tables and may need expansion.

---

## G) Deployment & Demo (10)

### 1) App runs without error (5)

What it is:
Application should start and run reliably.

Where in your code/config:
- Dockerfile
- compose.yaml
- src/main/resources/application.yaml

How it works now:
Dockerized app stack starts successfully via compose in this session. Test execution in local Maven failed due to JAVA_HOME environment setup on machine, not necessarily code logic.

Current status:
Partially Fulfilled

### 2) Proper demonstration (5)

What it is:
Clear live demo of role-based flows and core features.

Where in your project:
- UI templates in src/main/resources/templates
- Controllers in src/main/java/com/example/Book_Exchange/controller

How it works now:
Demo flow can be shown locally through login/register, seller book management, buyer browse/save, and admin pages. Public deployment demo evidence is not yet present in current snapshot.

Current status:
Partially Fulfilled

---

## H) Documentation (5)

### Clear README (5)

What it is:
A clear README should explain setup, architecture, running, testing, and deployment.

Where in your code:
No README file found in current snapshot.

How it works now:
Documentation exists in PROJECT_EXPLANATION.md and MARKING_CRITERIA_REPORT.md, but rubric explicitly asks for README quality.

Current status:
Not Fulfilled

---

## Final Marking Risk Snapshot

Strong areas now:
- Security fundamentals
- Layered structure
- Dockerization and env config
- Core entity relationship modeling

High-risk areas for marks loss:
- Missing DTO and global exception handling
- Not enough unit test count for required threshold
- Missing true controller integration tests (SpringBootTest + MockMvc)
- Missing .github/workflows in current snapshot
- Missing README file
- Deployment proof/public URL not present in codebase
