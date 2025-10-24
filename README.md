# Clinic-API

[![CI - Java with Maven](https://github.com/BrunoLegal/clinic-api/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/BrunoLegal/clinic-api/actions/workflows/maven-ci.yml)

A RESTful API for managing a clinic's patients. This is a portfolio project built to demonstrate best practices in back-end development, including clean architecture (DTOs/Mappers), automated testing (Unit and Integration), and CI/CD automation. This project was built using Spring Boot 3 and Java 17, with a strong focus on automated testing and modern development practices.

## Features (Roadmap)

- [x] **Patient Management (CRUD)**
    - [x] Register new Patient (`POST /patients`)
    - [x] List all Patients (`GET /patients`)
    - [x] Get Patient details by ID (`GET /patients/{id}`)
    - [x] Update Patient data (`PUT /patients/{id}`)
    - [x] Delete Patient (Soft delete) (`DELETE /patients/{id}`)


## Technologies Used
- **Back-end:** Java 17, Spring Boot 3, Bean Validation (JSR 380)
- **Persistence:** Spring Data JPA, Hibernate, PostgreSQL
- **Testing:**
    - **Unit Tests:** JUnit 5, Mockito
    - **Integration Tests:** Spring Boot Test (`@SpringBootTest`), MockMvc, H2 Database
- **Build:** Maven
- **DevOps:**
    - **Local Environment:** Docker Compose (for PostgreSQL)
    - **CI/CD:** GitHub Actions (Build & Test Automation)
- **Other:** Lombok, Custom Mapper

## Project Architecture
The project follows a layered architecture to ensure separation of concerns and maintainability:

- **`Domain`**: Contains the core business entities (e.g., Patient).
- **`DTOs`**: Data Transfer Objects used for communication between layers, to ensure that only necessary data is exposed.
- **`Repository`**: Manages data persistence using Spring Data JPA.
- **`Service`**: Contains all business logic. It orchestrates operations between the controller and repository layers.
- **`Controller`**: Handles all incoming HTTP requests, validates input data(DTOs), and returns appropriate HTTP responses.
- **`Mapper`**: Responsible for converting between entity models and DTOs.
- **`Exceptions`**: Custom exception handling for better error management.

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

## API Documentation

### Patient Management

**`POST /patients`** - Register a new patient:

- **Request body:**
```json
{
    "name": "John Doe",
    "email": "johndoe@example.com",
    "phone": "123-456-7890"
}
```
- **Success Response:** `201 Created`
- **Error Responses:**
    - `400 Bad Request` - If validation fails (e.g., null fields, invalid email format).
    - `409 Conflict` - If a patient with the same email already exists.


--- 
**`GET /patients`**
Returns the details of all the **active** patients.
- **Success Response:** `200 OK`

---
**`GET /patients`**
Returns the details of a single **active** patient.
- **Success Response:** `200 OK`
- **Error Response:**
    - `404 Not Found`: If the patient with the specified ID does not exist or is inactive.
---

**`PUT /patients/{id}`**
Updates the data of an existing patient.

-   **Request Body:**
    ```json
    {
      "name": "John Updated Doe",
      "email": "john.new@example.com",
      "phone": "11777776666"
    }
    ```
-   **Success Response:** `200 OK`
-   **Error Responses:**
    -   `400 Bad Request`: If validation fails.
    -   `404 Not Found`: If no patient is found with the specified ID.
    -   `409 Conflict`: If the new email is already in use by *another* patient.

---
**`DELETE /patients/{id}`**
**Logically deletes (soft delete)** a patient by setting their status to `inactive`. The data is not physically removed from the database.

-   **Success Response:** `204 No Content`
-   **Error Response:**
    -   `404 Not Found`: If no patient is found with the specified ID.

