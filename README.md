# Employee Management System (EMS) - Backend API

[![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%2F%204.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-MySQL-blue.svg)](https://www.mysql.com/)

This repository contains the backend service for the **Employee Management System (EMS)**. Built with **Java 17** and **Spring Boot**, the application provides a robust, clean-architecture REST API to manage employees and departments, supporting dynamic profile image uploads, advanced search/filtering, and strict data validation.

The backend is configured to seamlessly communicate with the companion Angular frontend.

---

## 🚀 Key Features

- **Department Management**: Create and list company departments.
- **Employee Management**: Full CRUD operations for employee records.
- **Advanced Search & Filtering**: Filter employees by search term (name/code), department, and salary ranges.
- **Dynamic Image Upload**: Upload profile images for employees (stored as `LONGBLOB` in MySQL).
- **Type-safe Mapping**: Utilizes **MapStruct** for automatic DTO-to-entity mapping.
- **Robust Exception Handling**: Global exception handler for validation errors and business rule violations (e.g., duplicate unique codes).
- **Pre-configured CORS**: Built-in support for Angular frontend requests (`http://localhost:4200`).

---

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 17 | Core programming language |
| **Framework** | Spring Boot | Web, Data JPA, Validation, Thymeleaf |
| **Database** | MySQL | Production relational database |
| **Test DB** | H2 Database | In-memory database for unit/integration testing |
| **Boilerplate Reduction** | Lombok | Automated getters, setters, constructors, and builders |
| **DTO Mapping** | MapStruct | Compiles fast, type-safe entity-DTO conversions |
| **Build Tool** | Maven | Project management and build tool |

---

## 📁 Architecture & Directory Structure

The project follows a standard multi-layer architecture emphasizing separation of concerns:

```
ems-backend/
├── db_script.sql                  # Database creation and sample seed script
├── pom.xml                        # Maven dependencies & build configurations
└── src/
    ├── main/
    │   ├── java/com/example/ems/
    │   │   ├── EmsApplication.java # Spring Boot entrypoint
    │   │   ├── controller/         # REST Controllers (exposing endpoints)
    │   │   ├── dto/                # Data Transfer Objects for requests/responses
    │   │   ├── exception/          # Custom exceptions & global handler
    │   │   ├── mapper/             # MapStruct mapper interfaces
    │   │   ├── model/              # JPA Entity models
    │   │   ├── repository/         # Spring Data JPA Repository interfaces
    │   │   └── service/            # Service interfaces & implementation classes
    │   └── resources/
    │       ├── application.yaml    # Application settings (DB, multipart size, etc.)
    │       └── data.sql            # Automatically runs on startup to seed departments
    └── test/                       # Unit & integration test suites
```

---

## ⚙️ Setup & Configuration

### Prerequisites
- **JDK 17** installed and configured in your environment path.
- **MySQL Server** running locally on port `3306`.
- **Maven** (optional, Maven wrapper `./mvnw` is included).

### 1. Database Setup
Ensure MySQL is running. You can set up the database using the provided `db_script.sql`:

1. Log into your MySQL console or client.
2. Run the script file:
   ```sql
   SOURCE db_script.sql;
   ```
   *This creates a database named `ems_db`, sets up the `department` and `employee` tables, and seeds initial departments (`HR`, `IT`, `FIN`).*

### 2. Application Properties
The database credentials and configuration are defined in [application.yaml](src/main/resources/application.yaml). By default, it connects to a local MySQL instance:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ems_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: 
```
*Modify the `username` and `password` values if your local MySQL setup requires authentication.*

---

## 🏃 Running the Application

### Running in Development Mode
You can start the Spring Boot application using the Maven wrapper:

**On Linux/macOS:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

Once successfully started, the API will be available at `http://localhost:8080`.

### Building and Running the Production JAR
To compile the code and package it into a runnable JAR file:

```bash
# Build the project
./mvnw clean package

# Run the packaged JAR
java -jar target/ems-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Running Tests
The project features unit and integration tests using JUnit 5, Mockito, and Spring Boot Test utilities. Run the test suite with:

```bash
./mvnw test
```

---

## 📡 API Endpoints Reference

### Departments (`/api/departments`)

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/departments` | Retrieves all departments | *None* |
| **POST** | `/api/departments` | Creates a new department | `DepartmentDto` (JSON) |

### Employees (`/api/employees`)

| Method | Endpoint | Description | Query Parameters / Request |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/employees` | Lists employees (supports optional search/filters) | `searchTerm`, `departmentId`, `minSalary`, `maxSalary` |
| **GET** | `/api/employees/{id}` | Retrieves a single employee by ID | *None* |
| **POST** | `/api/employees` | Creates a new employee with optional image upload | `EmployeeRequestDto` (Multipart Form) + `imageFile` |
| **PUT** | `/api/employees/{id}`| Updates an existing employee with optional image | `EmployeeRequestDto` (Multipart Form) + `imageFile` |
| **DELETE**| `/api/employees/{id}`| Deletes an employee by ID | *None* |
| **GET** | `/api/employees/{id}/image` | Serves the profile image of an employee | *None* (Returns raw image byte array) |

---

## 🔒 Security & CORS
To allow integration with frontends running on different ports, Cross-Origin Resource Sharing (CORS) is explicitly enabled on all controllers for the Angular development server:
`@CrossOrigin(origins = "http://localhost:4200")`
