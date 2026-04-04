# Book Exchange Project - Simple Explanation Guide

This file is made so you can explain your project easily to your teacher.
It covers:
- What this project does
- How the system flows
- What has been completed so far
- How security, testing, Docker, and CI/CD work
- How to demo it step by step

## 1. Project Idea (In Simple Words)

This is a role-based Book Exchange web application.
Users can register and log in with one of three roles:
- ADMIN
- SELLER
- BUYER

Main idea:
- Sellers list books.
- Buyers request exchange for available books.
- Sellers can accept or reject those requests.
- Admin has protected access to admin area.

So this project demonstrates a complete software engineering workflow, not just coding:
- application development
- database design
- security
- testing
- Docker
- CI/CD pipeline

## 2. Tech Stack Used

Backend:
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf

Database:
- PostgreSQL (main runtime)
- H2 (for tests only)

Testing:
- JUnit 5
- Mockito
- MockMvc

DevOps:
- Docker + Docker Compose
- GitHub Actions (CI)
- Render deploy workflow file added for CD

## 3. Project Architecture (Layered)

The project is organized in layers:
- controller: handles HTTP requests (REST + MVC pages)
- service: contains business rules
- repository: talks to database using JPA
- entity: database table models
- dto: request/response data objects
- mapper: converts between entity and dto
- security: authentication and authorization settings
- exception: global error handling

This clean layering is good for maintainability and matches your rubric.

## 4. Database and Entity Flow

Implemented core entities:
- AppUser
- Role
- Book
- ExchangeRequest

Important relationships:
- One seller can have many books
- One buyer can make many exchange requests
- Each exchange request belongs to one book
- User and role are linked for role-based authorization

Exchange request lifecycle:
1. Buyer creates request for a book (status = PENDING)
2. Seller reviews request
3. Seller accepts or rejects
4. If accepted, book status changes to EXCHANGED

## 5. Security Flow (How Login and Roles Work)

Security has two parts:

1. Authentication (Who are you?)
- User logs in (form login)
- Spring Security loads user from database
- Passwords are encoded using BCrypt

2. Authorization (What can you access?)
- ADMIN routes: only admin
- SELLER routes: seller/admin
- BUYER routes: buyer/admin

Examples:
- Buyer cannot access seller dashboard
- Seller cannot access buyer-only pages
- Unauthorized users get blocked (401/403)

## 6. UI Flow (Thymeleaf Pages)

Added pages:
- Home page
- Login page
- Register page
- Seller dashboard
- Buyer requests page
- Admin panel

Simple user journey:
1. Open home page
2. Login with role user
3. Role-based navigation appears
4. Access allowed pages based on role

## 7. API Flow (REST)

Auth API:
- Register user
- Get current user info endpoint

Book API:
- Seller creates/updates/deletes own books
- Public book listing and book details

Exchange API:
- Buyer creates exchange request
- Seller responds (accept/reject)
- Buyer/seller can view request lists
- Buyer can delete own pending request

Global exception handler returns proper HTTP status for:
- conflict
- not found
- forbidden
- validation errors
- business validation errors

## 8. What Has Been Completed Phase-by-Phase

Phase 1 completed:
- project baseline setup
- Dockerfile + compose improvements
- env-based configuration
- CI workflow added

Phase 2 completed:
- user + role model
- registration
- Spring Security basic role protection
- initial auth tests

Phase 3 completed:
- book and exchange domain model
- business logic in services
- DTO and mapper structure
- service-layer unit tests

Phase 4 completed:
- REST controllers for book and exchange
- CRUD and role checks on endpoints
- integration tests for controller behavior

Phase 5 completed:
- Thymeleaf UI pages
- role-based page routing
- form login flow
- MVC route access tests

Phase 6 completed:
- stronger negative-path tests
- coverage report generation using JaCoCo
- CI artifact upload for test reports + coverage

## 9. Testing Status (Current)

Current test strategy includes:
- Unit tests for service layer
- Integration tests for REST controllers
- Integration tests for MVC page access by role

Test command:
- mvnw.cmd -B clean verify

Current outcome:
- All tests passing
- Coverage report generated in target/site/jacoco

## 10. CI/CD Flow (Simple)

CI (GitHub Actions):
- runs on push/pull_request to develop and main
- job 1: build-and-test
- job 2: docker-build
- uploads surefire and jacoco artifacts

CD (Render workflow file):
- deploy file is already added
- deployment should happen from main using Render hook secret

Branch workflow used:
- feature/* -> develop -> main
- main is protected
- approvals and required checks are enforced

## 11. How To Explain This to Teacher (Short Speaking Script)

You can say:

"This project is a full-stack Book Exchange platform using Spring Boot, PostgreSQL, and Thymeleaf. I implemented role-based security for ADMIN, SELLER, and BUYER. Sellers can manage books, buyers can request exchanges, and sellers can accept/reject requests. The project follows layered architecture with controller-service-repository pattern. I wrote unit and integration tests, and all tests run automatically in GitHub Actions before merge. The app is Dockerized with app plus PostgreSQL in compose, and CI/CD workflow is prepared for deployment from main branch."

## 12. Live Demo Checklist

Before demo:
1. Start postgres using Docker Compose
2. Run app
3. Login as seller, buyer, and admin
4. Show role-based page access
5. Show one API request flow (create book, create request, respond)
6. Show GitHub Actions checks passing

## 13. Current Limitations and Next Work

Still to finish for final submission quality:
- enrich README with full diagrams and endpoint table
- complete Render deployment and public URL verification
- polish UI for better presentation
- add any missing final business validations based on teacher feedback

---
If your teacher asks "How do you know it works?", answer:
- "Because role-based flows are tested automatically by unit and integration tests, and CI blocks merge when tests fail."
