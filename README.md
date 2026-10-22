# Clinic Management Backend

Spring Boot REST API for managing clinic operations, including patients, medical
records, medicines, prescriptions, and authenticated users.

## Technology Stack

- Java 25 (LTS)
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven

## Getting Started

### Prerequisites

- Java 25 or later
- PostgreSQL

### Configuration

The application reads database settings from environment variables:

| Variable | Default |
| --- | --- |
| `SERVER_PORT` | `8088` |
| `DB_URL` | `jdbc:postgresql://localhost:5432/phongkhamDB` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | Empty |
| `JPA_SHOW_SQL` | `true` |

Create the `phongkhamDB` database, then set `DB_PASSWORD` before starting the
application.

### Run

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

### Test

```bash
./mvnw test
```

## Project Structure

```text
src/main/java/com/example/phongkhambackend/
├── controller/   REST endpoints
├── dto/          API request and response objects
├── model/        JPA entities
├── repository/   Data access repositories
└── security/     Authentication and authorization configuration
```

## Authentication

The API uses stateless HTTP Basic authentication. Login is available at
`/api/login`; administrative endpoints under `/api/admin/**` require the
`ADMIN` role.
