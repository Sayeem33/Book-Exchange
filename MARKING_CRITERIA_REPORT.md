# Book Exchange Evaluation Rubric Mapping (Up to Date)

This document maps your marking criteria to the current implementation in this repository.
It is written so you can use it as a speaking guide in a viva/demo or as a submission note for grading.

For each rubric point, this report explains:
- What it is
- Where it is in your code
- How it works now
- Current status

Test status checked during this review: 42 tests passed, 0 failed.

## 1) Architecture & Code Quality (20 marks)

### 1.1 Layered architecture (5)
What it is:
A layered architecture means the application is split into clear responsibilities instead of putting all logic in one place. In a Spring Boot app, the usual structure is:
- controller: receives HTTP requests and returns responses
- service: contains business rules and decision-making
- repository: handles database access
- entity: represents database tables
- DTO: represents request and response payloads
- mapper: converts between entities and DTOs

This matters because each layer changes for a different reason. If the UI or API changes, you usually update controllers or DTOs. If a rule changes, you update services. If the database structure changes, you update entities and repositories. That separation keeps the project easier to maintain, test, and explain.

Where in your code:
- [src/main/java/com/example/Book_Exchange/controller](src/main/java/com/example/Book_Exchange/controller)
- [src/main/java/com/example/Book_Exchange/service](src/main/java/com/example/Book_Exchange/service)
- [src/main/java/com/example/Book_Exchange/repository](src/main/java/com/example/Book_Exchange/repository)
- [src/main/java/com/example/Book_Exchange/entity](src/main/java/com/example/Book_Exchange/entity)
- [src/main/java/com/example/Book_Exchange/dto](src/main/java/com/example/Book_Exchange/dto)
- [src/main/java/com/example/Book_Exchange/mapper](src/main/java/com/example/Book_Exchange/mapper)

How it works now:
The request flow is: controller receives data, service applies the business rule, repository reads or writes the database, and mapper prepares safe output for the client. For example, a seller creates a book through a controller endpoint, the controller forwards the request to BookService, the service creates a Book entity, and the repository saves it. The controller then returns a BookResponse instead of exposing the entity directly.

Because of this structure, the code stays focused. The controller does not know database details, and the repository does not know HTTP details. That is exactly what layered architecture is meant to achieve.

Current status:
Implemented.

### 1.2 Clean code & naming (5)
What it is:
Clean code means the code is easy to read, predictable, and hard to misuse. Good naming is part of that: class names, method names, and variable names should describe what they actually do. Small methods are also important because they reduce mental load and make bugs easier to find.

In practical terms, clean code is visible when you can understand the purpose of a method without reading every line. If a method is called createRequest, you should expect it to create a request. If a class is called GlobalExceptionHandler, you should expect centralized error handling. That kind of clarity makes the project feel professional and easier to grade.

Where in your code:
- [src/main/java/com/example/Book_Exchange/service/BookService.java](src/main/java/com/example/Book_Exchange/service/BookService.java)
- [src/main/java/com/example/Book_Exchange/service/ExchangeRequestService.java](src/main/java/com/example/Book_Exchange/service/ExchangeRequestService.java)
- [src/main/java/com/example/Book_Exchange/controller/ExchangeRequestController.java](src/main/java/com/example/Book_Exchange/controller/ExchangeRequestController.java)

How it works now:
Methods like createBook, updateBook, respondToRequest, getRequestsForBuyer, and deleteRequest are direct and action-oriented. Each method does one obvious job: it either creates, reads, updates, or deletes a domain object, or enforces a rule such as ownership or request status.

The exception classes also support readability. Names like ResourceNotFoundException and ForbiddenOperationException immediately communicate what failed. That makes the code self-documenting and helps the API behave consistently.

Current status:
Implemented.

### 1.3 DTO usage (5)
What it is:
DTO stands for Data Transfer Object. It is a simple object used to carry data between layers, especially between the API and the service layer. DTOs are useful because entities often contain persistence details, lazy-loaded relations, or fields you do not want to expose publicly.

DTOs also give you a place to enforce validation rules on incoming data. For example, a request DTO can require a title, email, or password format before the service logic ever runs. That prevents invalid data from moving deeper into the application.

Where in your code:
- [src/main/java/com/example/Book_Exchange/dto/book](src/main/java/com/example/Book_Exchange/dto/book)
- [src/main/java/com/example/Book_Exchange/dto/exchange](src/main/java/com/example/Book_Exchange/dto/exchange)
- [src/main/java/com/example/Book_Exchange/dto/auth](src/main/java/com/example/Book_Exchange/dto/auth)
- [src/main/java/com/example/Book_Exchange/mapper/BookMapper.java](src/main/java/com/example/Book_Exchange/mapper/BookMapper.java)
- [src/main/java/com/example/Book_Exchange/mapper/ExchangeRequestMapper.java](src/main/java/com/example/Book_Exchange/mapper/ExchangeRequestMapper.java)

How it works now:
The controllers accept request DTOs such as RegisterRequest, BookRequest, and ExchangeRequestCreateRequest. These DTOs are annotated with validation constraints, so bad input is rejected early with a 400 response. This keeps invalid requests from reaching the business logic.

On the output side, the app returns response DTOs such as BookResponse and ExchangeRequestResponse. The mapper classes convert entities into these DTOs so the client sees only the fields that matter. That avoids leaking internal database structure and gives you control over the response shape.

Current status:
Implemented.

### 1.4 Exception handling (5)
What it is:
Exception handling is how the application turns internal failures into useful API responses. Without centralized handling, every controller would need to repeat the same error logic, and clients would receive inconsistent responses.

In a web app, different failure types should map to different HTTP statuses. A missing record should be a 404, a duplicate username should be a 409, a validation failure should be a 400, and an unauthorized action should be a 403. That makes the API clearer and easier for front-end or API clients to consume.

Where in your code:
- [src/main/java/com/example/Book_Exchange/exception/GlobalExceptionHandler.java](src/main/java/com/example/Book_Exchange/exception/GlobalExceptionHandler.java)
- [src/main/java/com/example/Book_Exchange/exception](src/main/java/com/example/Book_Exchange/exception)

How it works now:
The GlobalExceptionHandler catches custom exceptions thrown by services. For example, if a book does not exist, the service throws ResourceNotFoundException and the handler returns a 404 with a JSON error body. If a buyer tries to delete another buyer's request, the service throws ForbiddenOperationException and the handler returns 403.

Validation errors are also collected into a structured response. That means the client can see exactly which field failed and why. This is better than returning a generic server error because it supports proper user feedback.

Current status:
Implemented.

## 2) Security & Role Management (15 marks)

### 2.1 Spring Security implemented (5)
What it is:
Spring Security is the framework that controls who can log in and what they are allowed to access. It sits in front of your controllers and checks authentication and authorization before requests reach the application logic.

Authentication answers the question "who are you?" Authorization answers "what are you allowed to do?" In this project both are important because the app has public pages, authenticated pages, and role-protected endpoints.

Where in your code:
- [src/main/java/com/example/Book_Exchange/security/SecurityConfig.java](src/main/java/com/example/Book_Exchange/security/SecurityConfig.java)
- [src/main/java/com/example/Book_Exchange/security/AppUserDetailsService.java](src/main/java/com/example/Book_Exchange/security/AppUserDetailsService.java)

How it works now:
The SecurityConfig class defines a SecurityFilterChain bean. It explicitly permits the home page, login page, register page, and static CSS, while protecting API and page routes by role. It also enables form login so users can sign in through the UI and HTTP basic for API-style access.

The AppUserDetailsService loads users from the database and converts their roles into Spring Security authorities. That is the bridge between your application tables and Spring Security's authorization engine.

Current status:
Implemented.

### 2.2 Password encryption (3)
What it is:
Password encryption is about protecting credentials at rest. In application code, this is usually done with hashing rather than reversible encryption. BCrypt is a strong one-way password hashing algorithm and is the standard choice in Spring Security applications.

This matters because if the database is ever exposed, hashed passwords are much harder to use maliciously than plain text passwords. The application never needs to know the raw password again after registration because login checks compare hashes.

Where in your code:
- [src/main/java/com/example/Book_Exchange/security/SecurityConfig.java](src/main/java/com/example/Book_Exchange/security/SecurityConfig.java)
- [src/main/java/com/example/Book_Exchange/service/UserService.java](src/main/java/com/example/Book_Exchange/service/UserService.java)
- [src/test/java/com/example/Book_Exchange/service/UserServiceTest.java](src/test/java/com/example/Book_Exchange/service/UserServiceTest.java)

How it works now:
The SecurityConfig class exposes a PasswordEncoder bean backed by BCryptPasswordEncoder. UserService.register() calls passwordEncoder.encode(request.getPassword()) before storing the user. That means the database stores a hash, not the raw password.

The UserServiceTest confirms this behavior by mocking the encoder and verifying that the saved password is the encoded value. That proves encryption is part of the registration flow, not just a configuration placeholder.

Current status:
Implemented.

### 2.3 Role-based access enforced (7)
What it is:
Role-based access control means permissions depend on the user's role. In this app, ADMIN has the broadest access, SELLER can manage books and respond to requests, and BUYER can browse books and create exchange requests.

This is one of the most important parts of the grading because it shows that the app is not just authenticated; it is authorization-aware. Different users experience different features based on business rules.

Where in your code:
- [src/main/java/com/example/Book_Exchange/security/SecurityConfig.java](src/main/java/com/example/Book_Exchange/security/SecurityConfig.java)
- [src/main/java/com/example/Book_Exchange/controller/RoleAccessController.java](src/main/java/com/example/Book_Exchange/controller/RoleAccessController.java)
- [src/test/java/com/example/Book_Exchange/controller/AuthControllerIntegrationTest.java](src/test/java/com/example/Book_Exchange/controller/AuthControllerIntegrationTest.java)
- [src/test/java/com/example/Book_Exchange/controller/PageControllerIntegrationTest.java](src/test/java/com/example/Book_Exchange/controller/PageControllerIntegrationTest.java)

How it works now:
SecurityConfig defines role rules by URL pattern. For example, /api/admin/** and /admin/** require ADMIN, /api/seller/** and /seller/** require SELLER or ADMIN, and /api/buyer/** and /buyer/** require BUYER or ADMIN.

The controller tests prove the rule enforcement in practice. A buyer gets forbidden on seller/admin routes, while the matching role can access the page or endpoint successfully. The AppUserDetailsService makes this work by translating stored roles into ROLE_ authorities that Spring Security understands.

Current status:
Implemented.

## 3) Testing (15 marks)

### 3.1 Unit tests quality (7)
What it is:
Unit tests are fast tests that isolate a single class or method and verify its business logic without starting the full web application. They are especially useful for services because services contain the actual domain rules: who can create, update, delete, accept, reject, or register.

Good unit tests do more than check the happy path. They also prove the error paths: duplicate data, missing records, forbidden operations, invalid status transitions, and role fallback behavior.

Where in your code:
- [src/test/java/com/example/Book_Exchange/service/BookServiceTest.java](src/test/java/com/example/Book_Exchange/service/BookServiceTest.java)
- [src/test/java/com/example/Book_Exchange/service/ExchangeRequestServiceTest.java](src/test/java/com/example/Book_Exchange/service/ExchangeRequestServiceTest.java)
- [src/test/java/com/example/Book_Exchange/service/UserServiceTest.java](src/test/java/com/example/Book_Exchange/service/UserServiceTest.java)

How it works now:
The service tests use Mockito to fake repositories and password encoding. That lets the tests focus on service logic only. For example, BookServiceTest checks that only the book owner can update or delete a book, and ExchangeRequestServiceTest checks that only pending requests can be responded to or deleted.

UserServiceTest verifies registration behavior, including duplicate username/email handling, default buyer role, requested seller role, and encoded passwords. These tests show that the service layer is enforcing business rules correctly instead of just storing data.

Current status:
Implemented.

### 3.2 Integration tests (5)
What it is:
Integration tests load the Spring context and exercise real request handling through MockMvc. They are more realistic than unit tests because they check controllers, security, validation, and database interaction together.

These tests matter because a project can have good service tests but still fail at the API layer if endpoints are misconfigured, security blocks the wrong route, or validation is not wired correctly.

Where in your code:
- [src/test/java/com/example/Book_Exchange/controller/AuthControllerIntegrationTest.java](src/test/java/com/example/Book_Exchange/controller/AuthControllerIntegrationTest.java)
- [src/test/java/com/example/Book_Exchange/controller/BookAndExchangeControllerIntegrationTest.java](src/test/java/com/example/Book_Exchange/controller/BookAndExchangeControllerIntegrationTest.java)
- [src/test/java/com/example/Book_Exchange/controller/PageControllerIntegrationTest.java](src/test/java/com/example/Book_Exchange/controller/PageControllerIntegrationTest.java)

How it works now:
The integration tests run with @SpringBootTest and the test profile, which swaps PostgreSQL for H2 and lets the app run in a controlled environment. MockMvc sends real HTTP-like requests into the application and checks response codes, views, and security behavior.

AuthControllerIntegrationTest verifies registration, validation failure, conflict behavior, and admin access control. BookAndExchangeControllerIntegrationTest checks seller and buyer flows for books and exchange requests. PageControllerIntegrationTest confirms that the Thymeleaf pages are accessible only to the right roles.

Current status:
Implemented.

### 3.3 Tests run in CI (3)
What it is:
Continuous Integration means your tests run automatically every time code is pushed or a pull request is opened. This protects the project from regressions and ensures the submitted code is not just passing locally on one machine.

For marking, this matters because it proves the project is being checked in a repeatable way. CI is part of the engineering discipline, not just the code itself.

Where in your code:
- [.github/workflows/ci.yml](.github/workflows/ci.yml)

How it works now:
The ci.yml workflow runs on push and pull request events for develop and main. It installs Java 17, caches Maven dependencies, runs ./mvnw -B clean verify, and uploads surefire and JaCoCo artifacts.

That means every CI run compiles the project, executes tests, and generates coverage data. If something breaks, the workflow fails before code is merged.

Current status:
Implemented.

## 4) Dockerization (10 marks)

### 4.1 Proper Dockerfile (4)
What it is:
The Dockerfile defines how to package the application into a container image. A good Dockerfile makes the app reproducible, so it can run the same way on a local machine, in CI, or in deployment.

In practice, this is important because Java applications depend on the correct runtime and build environment. Containers remove a lot of "works on my machine" problems.

Where in your code:
- [Dockerfile](Dockerfile)

How it works now:
Your Dockerfile uses a multi-stage build. The first stage uses Maven plus JDK to compile and package the app, and the second stage uses a smaller JRE image to run the final jar. This keeps the runtime image smaller and cleaner.

The image exposes port 8080 and starts the Spring Boot jar directly. That is a standard container pattern for Java apps.

Current status:
Implemented.

### 4.2 docker-compose works (4)
What it is:
Docker Compose is used when the app needs multiple containers that work together. For this project, that means the Spring Boot app and PostgreSQL database are started together with the right environment variables and startup order.

This is especially useful for local development and demo runs because you do not have to configure the database manually each time.

Where in your code:
- [compose.yaml](compose.yaml)

How it works now:
The compose file starts a PostgreSQL container with a persistent volume so data survives restarts. The app service builds from the local Dockerfile, points Spring to the postgres host name inside the Compose network, and waits for the database healthcheck before starting.

Because the datasource URL and credentials are injected through environment variables, the same compose file can be adapted for local demo or deployment-style setup.

Current status:
Implemented.

### 4.3 Environment config handled correctly (2)
What it is:
Environment configuration means the app does not hard-code environment-specific values like database credentials, ports, or profile behavior. Instead, it reads them from environment variables or profile blocks.

This is important because local development, testing, and production often need different settings. A clean config setup lets you change behavior without editing code.

Where in your code:
- [src/main/resources/application.yaml](src/main/resources/application.yaml)
- [compose.yaml](compose.yaml)
- [.env.example](.env.example)

How it works now:
application.yaml uses placeholders for datasource URL, username, password, and server port. The test profile switches to an H2 in-memory database and disables Docker Compose. The prod profile changes JPA to validate so the app checks schema consistency instead of modifying it automatically.

This means one codebase supports local development, testing, and production-like settings with different configuration blocks.

Current status:
Implemented.

## 5) CI/CD & Git Workflow (15 marks)

### 5.1 Branch protection configured (5)
What it is:
Branch protection is not an application feature; it is a GitHub repository rule. It typically requires successful CI checks, reviewed pull requests, and sometimes restrictions on who can push directly to main.

This matters because it prevents accidental direct pushes and ensures the team follows a controlled release process.

Where in your code:
Not stored in source files. This is configured in GitHub repository settings.

How it works now:
This cannot be confirmed from source files in the repository because it lives in GitHub settings, not in the application code.

Current status:
Not verifiable from this repository snapshot.

### 5.2 GitHub Actions workflow correct (5)
What it is:
GitHub Actions is the CI runner that automatically builds and checks your code in the cloud. A correct workflow should install dependencies, run tests, and fail loudly if anything breaks.

The idea is to make verification repeatable and visible to reviewers, not hidden on a developer's laptop.

Where in your code:
- [.github/workflows/ci.yml](.github/workflows/ci.yml)

How it works now:
The CI workflow sets up Java 17, makes the Maven wrapper executable, runs a full Maven verify, and uploads the test and coverage reports. A second job builds the Docker image after the test job succeeds.

This is a good structure because it blocks packaging if the code does not compile or tests fail.

Current status:
Implemented.

### 5.3 Automatic deployment working (5)
What it is:
Automatic deployment means code merged into the main branch can trigger a deployment pipeline without manual steps. In real projects, this is how you connect source control to a live environment.

For grading, the important part is not just that a deploy workflow exists, but that it actually runs successfully and reaches the hosting platform.

Where in your code:
- [.github/workflows/deploy-render.yml](.github/workflows/deploy-render.yml)

How it works now:
The deploy-render.yml workflow triggers on pushes to main and sends a POST request to the Render deploy hook stored in a secret. If the secret is configured and the Render service is valid, deployment is initiated automatically.

The repository shows the deployment mechanism, but the actual success of the deployment should be confirmed in GitHub Actions history and the Render dashboard.

Current status:
Partially verifiable from code (workflow exists; runtime deployment success must be checked in Actions history/Render logs).

## 6) Database Design (10 marks)

### 6.1 Proper entity relationships (10)
What it is:
Database design is about modeling the business rules in tables and relationships. Good relationships reduce duplication, keep data consistent, and make queries and access control easier to reason about.

The entity model should reflect the real world of your application: users have roles, sellers own books, and buyers create exchange requests against books.

Where in your code:
- [src/main/java/com/example/Book_Exchange/entity/AppUser.java](src/main/java/com/example/Book_Exchange/entity/AppUser.java)
- [src/main/java/com/example/Book_Exchange/entity/Role.java](src/main/java/com/example/Book_Exchange/entity/Role.java)
- [src/main/java/com/example/Book_Exchange/entity/Book.java](src/main/java/com/example/Book_Exchange/entity/Book.java)
- [src/main/java/com/example/Book_Exchange/entity/ExchangeRequest.java](src/main/java/com/example/Book_Exchange/entity/ExchangeRequest.java)

How it works now:
AppUser and Role are linked with a many-to-many join table called user_roles, which supports users having one or more roles. Book has a many-to-one relationship to AppUser because many books can belong to one seller. ExchangeRequest has many-to-one links to both Book and AppUser because many requests can refer to the same book and many requests can be created by the same buyer.

The use of enums for status fields keeps state values limited and predictable. For example, BookStatus and ExchangeRequestStatus prevent random strings from being stored in the database.

Current status:
Implemented.

## 7) Deployment & Demo (10 marks)

### 7.1 App runs without error (5)
What it is:
This criterion is about runtime stability. The app should start, connect to its dependencies, serve requests, and not crash during normal use.

From a grading perspective, it usually means the application is not just written correctly, but actually runnable in the expected environment.

Where in your code/evidence:
- [pom.xml](pom.xml)
- [src/test](src/test)
- [target/surefire-reports](target/surefire-reports)

How it works now:
The current automated test run passed, which is strong evidence that the application logic is stable. The repository also contains prior build output and surefire reports, which support that the project has been built before.

That said, this review did not execute a full local Maven verify inside the tool run, so the safest statement is that the app appears buildable and testable, but final runtime confirmation should be done with an actual launch command in your environment.

Current status:
Likely implemented, but full local run command should be executed once to confirm end-to-end startup in your environment.

### 7.2 Proper demonstration (5)
What it is:
Proper demonstration means you can present the app in a logical order and show the important features without confusion. A good demo typically covers authentication, role access, the main business flow, and one or two negative cases.

The grading here is usually based on clarity: can you show the app works, and can you explain why the design choices matter.

Where in your code/docs:
- [PROJECT_EXPLANATION.md](PROJECT_EXPLANATION.md)
- [src/main/resources/templates](src/main/resources/templates)
- [src/main/java/com/example/Book_Exchange/controller](src/main/java/com/example/Book_Exchange/controller)

How it works now:
The project already includes a demo checklist and a short speaking script in PROJECT_EXPLANATION.md. The Thymeleaf pages and controllers support a live walkthrough where you can log in as seller, buyer, or admin and demonstrate different access paths.

Because the app has distinct role-based pages and APIs, it is suitable for a structured demo: register, log in, create a book, create a request, respond to it, and show the role checks.

Current status:
Implemented (presentation readiness depends on rehearsal and deployed/live environment).

## 8) Documentation (5 marks)

### 8.1 Clear README (5)
What it is:
The README is the first place a reviewer looks for project understanding and setup instructions. A strong README explains what the app does, how to run it, how the architecture is organized, and how to test or deploy it.

For grading, the README should reduce friction. Someone should be able to open the repo and understand what the project is and how to run it without guessing.

Where in your code:
- [HELP.md](HELP.md)
- [PROJECT_EXPLANATION.md](PROJECT_EXPLANATION.md)

How it works now:
You already have useful documentation in PROJECT_EXPLANATION.md and HELP.md. The explanation file covers architecture, security, testing, Docker, CI/CD, and demo flow in a teacher-friendly format.

However, because there is no root README.md in the repository, the documentation mark may be weaker than it could be. A README would make the project easier to review quickly.

Current status:
Partial. Content exists, but rubric usually expects a root README.md.

## Quick Rubric Highlight Summary

Strongly implemented and evidenced in code:
- Layered architecture
- DTO + mapper pattern
- Global exception handling
- Spring Security + role authorization + password encryption
- Unit and integration tests
- CI test execution and artifacts
- Dockerfile + compose + env profile configs
- Entity relationships

Needs external verification or improvement for full marks:
- Branch protection (must verify in GitHub settings)
- Auto deployment success (must verify action run + Render logs)
- Add a root README.md tailored to rubric expectations

If you want to present this to a teacher, the easiest summary is:
This project is a layered Spring Boot application with secure role-based access, validated DTOs, centralized error handling, entity relationships for books and exchange requests, strong test coverage, and Docker/CI support for repeatable builds.