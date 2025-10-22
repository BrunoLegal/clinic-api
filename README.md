# Clinic-API

[![CI - Java with Maven](https://github.com/BrunoLegal/clinic-api/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/BrunoLegal/clinic-api/actions/workflows/maven-ci.yml)

A RESTful API for managing a clinic's patients. This is a portfolio project built to demonstrate best practices in back-end development, including clean architecture (DTOs/Mappers), automated testing (Unit and Integration), and CI/CD automation. This project was built using Spring Boot 3 and Java 17, with a strong focus on automated testing and modern development practices.

## Features (Roadmap)

- [x] **Patient Management (CRUD)**
    - [x] Register new Patient (`POST /patients`)
    - [x] List all Patients (`GET /patients`)
    - [ ] Get Patient details by ID (`GET /patients/{id}`)
    - [ ] Update Patient data (`PUT /patients/{id}`)
    - [ ] Delete Patient (Logical Deletion) (`DELETE /patients/{id}`)
- [ ] **Appointment Management (CRUD)**
    - [ ] Schedule new appointment

## Technologies Used
- **Back-end:** Java 17, Spring Boot 3
- **Persistence:** Spring Data JPA, Hibernate, PostgreSQL
- **Testing:**
    - **Unit Tests:** JUnit 5, Mockito
    - **Integration Tests:** Spring Boot Test (`@SpringBootTest`), MockMvc, H2 Database
- **Build:** Maven
- **DevOps:**
    - **Local Environment:** Docker Compose (for PostgreSQL)
    - **CI/CD:** GitHub Actions (Build & Test Automation)
- **Other:** Lombok, Custom Mapper

## How to Run Locally
1.  Ensure you have Java 17 (JDK) and Docker installed.
2.  Clone the repository:
    ```bash
    git clone https://github.com/brunolegal/clinic-api.git
    cd clinic-api
    ```
3.  Start the PostgreSQL database using Docker:
    ```bash
    docker-compose up -d
    ```
4.  Run the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    ```
5.  The application will be available at `http://localhost:8080`.

## API Endpoints
| Method | Endpoint         | Description                     |
|--------|------------------|---------------------------------|
| `POST` | `/patients`      | Registers a new patient         |
| `GET`  | `/patients`      | Lists all patients              |
| `PUT`  | `/patients/{id}` | Updates specific patient (Soon) |
