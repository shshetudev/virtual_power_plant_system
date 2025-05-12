### Business Requirements

- You are working on a virtual power plant system for aggregating distributed power sources into a single cloud-based
  energy provider.
- A virtual power plant (VPP) is a system that integrates multiple, possibly heterogeneous, power resources to provide
  grid power.
- A VPP typically sells its output to an electric utility.
- VPPs allow energy resources that are individually too small to be of interest to a utility to aggregate and market
  their power.
- As of 2024, VPPs are operated in the United States, Europe, and Australia.

### Technical Requirements

**Mandatory:**

- Implement a REST API in Spring Boot.
- The API should have an endpoint that accepts, in the HTTP request body, a list of batteries, each containing: name,
  postcode, and watt capacity.
- This data should be persisted in a database (eg, MySQL, PostgreSQL, Redis, etc).
- The API should have an endpoint that receives a postcode range. The response body will contain a list of names of
  batteries that fall within the range, sorted alphabetically.
- Additionally, there should be statistics included for the returned batteries, such as total and average watt capacity.
- The implementation should use Java streams in some way.
- The project should have unit tests with at least 70% test coverage.

**Optional:**

- Extend the postcode range query to allow filtering based on minimum or maximum watt capacity.
- Integrate a logging framework and log significant system events.
- Ensure the system can handle a large number of battery registrations concurrently.
- Include integration tests with the use of test containers, for example, testing repository classes with the selected
  database.

### Project Structure

```
virtual_power_grid_system
 ├── .dockerignore
 ├── .git
 │   └── ... (Git related files)
 ├── .gitignore
 ├── Dockerfile
 ├── README.md
 ├── build.gradle
 ├── gradle
 │   └── wrapper
 │       ├── gradle-wrapper.jar
 │       └── gradle-wrapper.properties
 ├── gradlew
 ├── gradlew.bat
 ├── jacoco.gradle
 ├── settings.gradle
 └── src
     ├── main
     │   ├── java
     │   │   └── com
     │   │       └── powerledger
     │   │           └── io
     │   │               └── virtual_power_grid_system
     │   │                   ├── VirtualPowerGridSystemApplication.java
     │   │                   ├── battery
     │   │                   │   ├── BatteryController.java
     │   │                   │   ├── BatteryRegistrationListener.java
     │   │                   │   ├── BatteryService.java
     │   │                   │   ├── config
     │   │                   │   │   └── BatteryConfig.java
     │   │                   │   ├── dto
     │   │                   │   │   ├── BatteryDto.java
     │   │                   │   │   ├── BatteryRangeResponseDto.java
     │   │                   │   │   └── BatteryRequestDto.java
     │   │                   │   ├── model
     │   │                   │   │   └── Battery.java
     │   │                   │   ├── queue
     │   │                   │   │   └── BatteryRegistrationProducer.java
     │   │                   │   └── repository
     │   │                   │       └── BatteryRepository.java
     │   │                   └── common
     │   │                       ├── config
     │   │                       │   └── AsyncConfig.java
     │   │                       ├── constants
     │   │                       │   └── ResponseMessages.java
     │   │                       ├── dto
     │   │                       │   └── Response.java
     │   │                       └── exception
     │   │                           ├── GlobalExceptionHandler.java
     │   │                           └── ResourceNotFoundException.java
     │   └── resources
     │       ├── application.properties
     │       └── logback-spring.xml
     └── test
         ├── java
         │   └── com
         │       └── powerledger
         │           └── io
         │               └── virtual_power_grid_system
         │                   ├── battery
         │                   │   ├── integration_tests
         │                   │   │   ├── BatteryControllerIntegrationTest.java
         │                   │   │   └── BatteryRepositoryIntegrationTest.java
         │                   │   └── unit_tests
         │                   │       └── BatteryServiceTest.java
         │                   └── common
         │                       └── integration_tests
         │                           └── BaseRepositoryIntegrationTest.java
         └── resources
             └── application-test.properties
```

### Architectural Decisions

- **Layered Architecture:** The project follows a clear separation of concerns with Controller, Service, Repository, and
  DTO layers, promoting maintainability and testability.
- **Spring Boot & Spring Data JPA:** Chosen for rapid REST API development, dependency injection, and simplified
  database access.
- **Event-Driven Design:** Utilizes Redis and message queues for asynchronous battery registration, supporting
  scalability and decoupling.
- **Integration Testing with Testcontainers:** Ensures realistic database and Redis testing environments using Docker
  containers, improving test reliability.
- **In-Memory Database for Unit Tests:** Unit tests run with in-memory or mocked repositories to ensure fast, isolated
  testing.
- **Java Streams:** Used for efficient data processing and filtering, especially for battery queries and statistics.
- **Logging:** Centralized and structured logging with Logback, including log rotation and multi-destination outputs.
- **API Documentation:** Swagger/OpenAPI is integrated for interactive API exploration and documentation.
- **Code Quality:** Enforced minimum test coverage (70%+) with Jacoco, and code style consistency with clear commit
  conventions.

### How to run the project?

- Run `docker compose up`
- See the api documentation at: http://localhost:8080/swagger-ui.html
- Import the Postman collection from `resources/postman_collection/vpp_collection.postman_collection.json` folder

### Technology Used

- Java: 21
- Spring Boot: 3+
- Gradle
- PostgreSQL (via Testcontainers for integration tests)
- Redis (via Testcontainers for integration tests)
- Flyway
- REST API
- Jacoco: Ensured the build will never pass if test coverage is below 70%
- Logback
- JUnit
- Mockito
- Docker
- Spring Data JPA
- Event Driven Architecture (Using Redis)
- Tests written for all layers(Repository, Service, Controller): Ensured 100% coverage
    - Unit tests
    - Integration test

### Git commit types

- feat: a new feature
- fix: a bug fix
- docs: documentation changes
- style: formatting, missing semi colons, etc.
- refactor: code change that neither fixes a bug nor adds a feature
- test: adding or correcting tests
- chore: maintenance tasks

### Wireframe

- Swagger UI at: http://localhost:8080/swagger-ui.html
- OpenAPI JSON at: http://localhost:8080/api-docs

## Added Logging support(`logback-spring.xml`)

- Configures three logging destinations: console output, a general log file (virtual-power-grid.log), and a separate
  error-only log file (error.log)
- Implements log rotation with 30-day retention policy and size caps (3GB for general logs, 1GB for error logs)
- Sets specific logging levels: INFO for application code, WARN for Spring and Hibernate framework code
- Uses a consistent timestamp format with thread name, log level, logger name, and message across all logging outputs

### Necessary commands:

- Run the tests: `./gradlew test`
- Run the tests and see the jacoco report: `./gradlew jacocoTestReport`
- See the Jacoco report in UI:
  `http://localhost:63342/virtual_power_grid_system/build/reports/jacoco/test/html/index.html`
- Build the project: `./gradlew clean build`
- Run the application: `./gradlew clean build && ./gradlew bootRun`

