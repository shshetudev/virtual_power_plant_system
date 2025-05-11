### Business Requirements
- You are working on a virtual power plant system for aggregating distributed power sources into a single cloud-based energy provider. 
- A virtual power plant (VPP) is a system that integrates multiple, possibly heterogeneous, power resources to provide grid power. 
- A VPP typically sells its output to an electric utility. 
- VPPs allow energy resources that are individually too small to be of interest to a utility to aggregate and market their power. 
- As of 2024, VPPs are operated in the United States, Europe, and Australia.

### Technical Requirements
**Mandatory:**
- Implement a REST API in Spring Boot.
- The API should have an endpoint that accepts, in the HTTP request body, a list of batteries, each containing: name, postcode, and watt capacity. 
- This data should be persisted in a database (eg, MySQL, PostgreSQL, Redis, etc).
- The API should have an endpoint that receives a postcode range. The response body will contain a list of names of batteries that fall within the range, sorted alphabetically. 
- Additionally, there should be statistics included for the returned batteries, such as total and average watt capacity.
- The implementation should use Java streams in some way.
- The project should have unit tests with at least 70% test coverage.

**Optional:**
- Extend the postcode range query to allow filtering based on minimum or maximum watt capacity. 
- Integrate a logging framework and log significant system events.
- Ensure the system can handle a large number of battery registrations concurrently.
- Include integration tests with the use of test containers, for example, testing repository classes with the selected database.

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

### Necessary commands:
- Run the tests: `./gradlew test`
- Run the tests and see the jacoco report: `./gradlew jacocoTestReport`
- See the Jacoco report in UI: `http://localhost:63342/virtual_power_grid_system/build/reports/jacoco/test/html/index.html`
- Build the project: `./gradlew clean build`
- Run the application: `./gradlew clean build && ./gradlew bootRun`
