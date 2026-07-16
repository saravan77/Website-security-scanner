# Website Security Scanner

A production-ready Spring Boot 3.x boilerplate project with a clean, layered architecture.

## Tech Stack
* **Java**: Version 21
* **Framework**: Spring Boot 3.x
* **Build Tool**: Maven
* **Database**: MySQL (Production), H2 (Testing)
* **Frontend**: Thymeleaf, Bootstrap 5, Vanilla CSS & JS

## Project Structure
```
website-security-analyzer/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/securityanalyzer/
    │   │   ├── WebsiteSecurityAnalyzerApplication.java
    │   │   ├── config/             # Configuration classes
    │   │   ├── controller/         # Web/REST controllers
    │   │   ├── dto/                # Data Transfer Objects
    │   │   ├── entity/             # JPA Entities
    │   │   ├── exception/          # Global & custom exception handling
    │   │   ├── repository/         # Data repositories (Spring Data JPA)
    │   │   ├── service/            # Business logic interfaces & implementations
    │   │   └── util/               # Helper utilities
    │   └── resources/
    │       ├── static/
    │       │   ├── css/            # Custom CSS stylesheets
    │       │   └── js/             # Custom JavaScript files
    │       ├── templates/          # Thymeleaf views (HTML)
    │       └── application.properties
    └── test/
        ├── java/com/securityanalyzer/
        │   └── WebsiteSecurityAnalyzerApplicationTests.java
        └── resources/
            └── application-test.properties
```

## Getting Started

### Prerequisites
* Java 21 SDK
* Maven 3.8+

### Setup & Run
1. Configure your MySQL credentials in `src/main/resources/application.properties`.
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the web interface at `http://localhost:8080`.
