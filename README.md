# Banking Application

A full-stack banking application portfolio project built with Java, Spring Boot, Spring Security, PostgreSQL, and a React + TypeScript frontend.

**Portfolio status: Project 1 core milestone complete.** The project is being presented as a completed portfolio milestone, with integration hardening and additional capabilities kept in a clearly identified follow-up backlog. This is not a claim that every feature is production-ready.

## Technology stack

- Java 21 and Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Security and BCrypt password hashing
- React, TypeScript, Vite, and Axios (separate frontend repository)

## Implemented core

- Customer registration endpoint (`POST /api/users/register`)
- Customer account creation and account listing endpoints
- Persistence for users and accounts through PostgreSQL/JPA
- Spring Security configuration with a customer/admin role foundation
- Frontend login page, protected routes, and customer/admin dashboard structure

## Follow-up backlog

The following items are future improvements or integration work, not claims of fully completed functionality:

- Align the frontend login contract with backend authentication; complete and verify JWT authentication if retained as the frontend contract
- Finish and test end-to-end deposit, withdrawal, transfer, beneficiary lookup/save, and deposit-request flows
- Complete admin operations and role-based authorization tests
- Add automated integration tests, API documentation, and deployment instructions
- Add production-readiness checks for configuration, error handling, logging, and monitoring

## Future feature: Investment tools

Investment functionality is **planned for a future version**, not part of the current implementation. A future enhancement could provide educational investment information, simple return calculations, and visualisations. Any future implementation should clearly distinguish illustrative calculations from financial advice.

## Running locally

1. Install JDK 21, Maven, and PostgreSQL.
2. Create a PostgreSQL database named `banking_db`.
3. Configure database credentials through environment variables or a local, uncommitted configuration file. Do not commit real passwords, session cookies, tokens, or personal/customer data.
4. Run the backend with the Maven wrapper: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
5. Run the frontend from its separate repository using its documented npm scripts.

The backend currently uses a local PostgreSQL URL in its application configuration; review and externalise environment-specific settings before deployment.

## Related repository

Frontend: https://github.com/Bakang9911/banking_app_frontend

## Security note

This is an educational portfolio project, not a production banking service. Do not use real customer data or real financial credentials. Before deployment, complete authentication/API integration, security testing, configuration externalisation, and transaction integrity checks.
