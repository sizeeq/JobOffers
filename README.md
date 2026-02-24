<div align="center">

# 💼 JobOffers

**REST API for Java developer job offer aggregation**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?style=flat-square&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat-square&logo=springsecurity)
![MongoDB](https://img.shields.io/badge/MongoDB-4.2-47A248?style=flat-square&logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=flat-square&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![Tests](https://img.shields.io/badge/Tests-Unit%20%2B%20Integration-22c55e?style=flat-square)

A backend application that automatically fetches job offers for Java developers from an external HTTP API, stores them in MongoDB, and exposes a secured REST API with JWT authentication.

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [API Endpoints](#-api-endpoints)
- [Application Flow](#-application-flow)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Testing](#-testing)
- [Project Structure](#-project-structure)

---

## 🔍 Overview

JobOffers solves a common pain point — manually checking multiple job boards. The application:

- **Automatically fetches** new job offers from an external HTTP API on a scheduled interval
- **Deduplicates** offers based on URL — no duplicates in the database
- **Secures** all endpoints with JWT tokens — register → login → use
- **Lets users add** their own offers manually via REST API
- **Supports pagination** for listing offers (`GET /offers?page=0&size=10`)
- **Caches** data with Redis to reduce database load

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Infrastructure                     │
│                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │ REST         │  │ Security     │  │ Scheduler │  │
│  │ Controllers  │  │ (JWT Filter) │  │ (3h tick) │  │
│  └──────┬───────┘  └──────┬───────┘  └─────┬─────┘  │
│         │                 │                │        │
├─────────┼─────────────────┼────────────────┼────────┤
│                    Domain                           │
│                                                     │
│  ┌──────▼─────────────────▼────────────────▼─────┐  │
│  │            OfferFacade / UserFacade            │  │
│  └──────────────────┬────────────────────────────┘  │
│                     │                               │
│  ┌──────────────────▼────────────────────────────┐  │
│  │     OfferService  │  Domain Repositories       │  │
│  │     (business logic, deduplication)            │  │
│  └──────────────────┬────────────────────────────┘  │
│                     │                               │
├─────────────────────┼───────────────────────────────┤
│                Infrastructure (DB / Cache / HTTP)   │
│                                                     │
│  ┌───────────┐  ┌──────────┐  ┌───────────────────┐ │
│  │  MongoDB  │  │  Redis   │  │ HTTP Offer Fetcher │ │
│  │  (offers, │  │  Cache   │  │ (external API)    │ │
│  │   users)  │  │          │  │                   │ │
│  └───────────┘  └──────────┘  └───────────────────┘ │
└─────────────────────────────────────────────────────┘
```

Key design decisions:
- **Facade pattern** — `OfferFacade` and `UserFacade` are the only public entry points to the domain; infrastructure never accesses `OfferService` directly
- **Dependency inversion** — `OfferFetcher` and `OfferRepository` are interfaces owned by the domain; implementations live in infrastructure
- **Clean layering** — domain has zero imports from `infrastructure.*`

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.5 |
| Security | Spring Security + JWT (auth0 java-jwt) |
| Database | MongoDB |
| Cache | Redis |
| HTTP Client | RestTemplate (WebClient-ready) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers, WireMock, Awaitility |
| Build | Maven |
| Infrastructure | Docker Compose |

---

## 📡 API Endpoints

### 🔓 Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/register` | Register a new user |
| `POST` | `/token` | Authenticate and receive JWT token |

### 🔐 Protected (requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/offers` | List all offers (paginated) | `200 OK` |
| `GET` | `/offers?page=0&size=10` | Paginated offers | `200 OK` |
| `GET` | `/offers/{id}` | Get offer by ID | `200 OK` / `404 NOT_FOUND` |
| `POST` | `/offers` | Add a new offer manually | `201 CREATED` |
| `PUT` | `/offers/{id}` | Update an existing offer | `200 OK` / `404 NOT_FOUND` |
| `DELETE` | `/offers/{id}` | Delete an offer | `204 NO_CONTENT` |

### 📄 Request / Response Examples

<details>
<summary><b>POST /register</b></summary>

```json
// Request
{
  "username": "jakub",
  "password": "securePassword123"
}

// Response 201 Created
{
  "id": "64a1b2c3d4e5f6a7b8c9d0e1",
  "username": "jakub",
  "isCreated": true
}
```
</details>

<details>
<summary><b>POST /token</b></summary>

```json
// Request
{
  "username": "jakub",
  "password": "securePassword123"
}

// Response 200 OK
{
  "username": "jakub",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
</details>

<details>
<summary><b>GET /offers?page=0&size=10</b></summary>

```json
// Response 200 OK
{
  "content": [
    {
      "id": "64a1b2c3d4e5f6a7b8c9d0e1",
      "company": "TechCorp",
      "position": "Junior Java Developer",
      "salary": "8 000 – 12 000 PLN",
      "offerUrl": "https://techcorp.pl/jobs/123"
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
```
</details>

<details>
<summary><b>POST /offers</b></summary>

```json
// Request (Authorization: Bearer <token>)
{
  "company": "MyStartup",
  "position": "Java Developer",
  "salary": "10 000 PLN",
  "offerUrl": "https://mystartup.pl/jobs/java"
}

// Response 201 Created
// Location: /offers/64a1b2c3d4e5f6a7b8c9d0e2
{
  "id": "64a1b2c3d4e5f6a7b8c9d0e2",
  "company": "MyStartup",
  "position": "Java Developer",
  "salary": "10 000 PLN",
  "offerUrl": "https://mystartup.pl/jobs/java"
}
```
</details>

---

## 🔄 Application Flow

```
User                    System                   External API
 │                        │                           │
 │  POST /register        │                           │
 │───────────────────────>│                           │
 │  201 Created           │                           │
 │<───────────────────────│                           │
 │                        │                           │
 │  POST /token           │                           │
 │───────────────────────>│                           │
 │  200 OK + JWT          │                           │
 │<───────────────────────│                           │
 │                        │                           │
 │  GET /offers (+ JWT)   │   [Scheduler fires]       │
 │───────────────────────>│──── GET /offers ─────────>│
 │                        │<─── 200 [{...}, {...}] ───│
 │  200 OK [offers]       │   (dedup + save to DB)    │
 │<───────────────────────│                           │
```

The scheduler fires on a configurable interval (default: every 3 hours). It fetches all offers from the external API, filters out duplicates by `offerUrl`, and saves only new ones to MongoDB.

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker + Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/sizeeq/JobOffers.git
cd JobOffers
```

### 2. Start infrastructure (MongoDB + Redis)

```bash
docker-compose up -d
```

This starts:
- **MongoDB** on `localhost:27017`
- **Mongo Express** (DB browser) on `localhost:8081` — login: `express` / `express`
- **Redis** on `localhost:6379`

### 3. Set environment variables

```bash
export JWT_SECRET=your-secret-key-minimum-32-characters
```

Or create a `.env` file in the project root:

```
JWT_SECRET=your-secret-key-minimum-32-characters
```

### 4. Run the application

```bash
mvn spring-boot:run
```

### 5. Explore the API

Open Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## ⚙️ Configuration

`src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:admin@localhost:27017/joboffers

offers:
  http:
    client:
      config:
        uri: http://example-job-api.com
        port: 5057
        path: /offers
        connectionTimeout: 5000
        readTimeout: 5000
        fixedDelay: PT3H        # scheduler interval (ISO-8601 duration)

  jwt:
    config:
      secret: ${JWT_SECRET}     # set via environment variable
      issuer: Job Offers Service
      expirationDays: 30
```

---

## 🧪 Testing

The project has three levels of tests:

```
src/
├── test/                        # Unit tests (fast, no Spring context)
│   └── domain/
│       ├── offer/OfferFacadeTest.java      # 7 tests
│       └── user/UserFacadeTest.java        # 4 tests
│
└── integration/                 # Integration tests (full Spring context)
    ├── feature/
    │   └── HappyPathScenarioTest.java      # Full end-to-end scenario
    ├── apivalidationerror/
    │   └── ApiValidationFailedIntegrationTest.java
    ├── controller/error/
    │   └── DuplicateOfferUrlExceptionIntegrationTest.java
    └── http/error/
        └── OfferFetcherClientErrorIntegrationTest.java
```

### Run all tests

```bash
mvn test
```

### Unit tests — no Spring, no DB

Domain tests use `InMemoryOfferRepository` and `InMemoryUserRepository` instead of mocking the entire Spring Data layer. This makes tests fast and focused purely on business logic.

```java
@BeforeEach
void setUp() {
    offerRepository = new InMemoryOfferRepository();
    offerFacade = new OfferFacade(offerRepository, offerFetcher);
}
```

### Integration tests — Testcontainers + WireMock

Integration tests spin up a real MongoDB via Testcontainers and mock the external HTTP API with WireMock:

```java
@SpringBootTest
@Testcontainers
class HappyPathScenarioTest extends BaseIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2");

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();
}
```

The `HappyPathScenarioTest` covers a full 16-step user journey: from empty database, through registration, login, scheduler runs, fetching offers, to adding a custom offer.

---

## 📁 Project Structure

```
src/
├── main/java/pl/joboffers/
│   ├── domain/
│   │   ├── offer/
│   │   │   ├── Offer.java                  # domain entity
│   │   │   ├── OfferFacade.java            # public API of offer domain
│   │   │   ├── OfferService.java           # business logic (package-private)
│   │   │   ├── OfferFetcher.java           # port (interface)
│   │   │   ├── OfferRepository.java        # port (interface)
│   │   │   ├── OfferMapper.java
│   │   │   ├── dto/
│   │   │   │   ├── OfferDto.java
│   │   │   │   ├── OfferRequestDto.java
│   │   │   │   └── OfferUpdateRequestDto.java
│   │   │   └── exception/
│   │   │       ├── OfferNotFoundException.java
│   │   │       └── OfferAlreadyExistsException.java
│   │   └── user/
│   │       ├── User.java
│   │       ├── UserFacade.java
│   │       ├── UserMapper.java
│   │       ├── UserRepository.java
│   │       ├── dto/
│   │       └── exception/
│   │
│   └── infrastructure/
│       ├── offer/
│       │   ├── OfferRestController.java
│       │   ├── client/                     # HTTP fetcher implementation
│       │   ├── error/                      # @ControllerAdvice handlers
│       │   └── scheduler/                  # OfferScheduler
│       ├── security/
│       │   ├── SecurityConfig.java
│       │   ├── JwtAuthTokenFilter.java
│       │   ├── JwtAuthenticator.java
│       │   ├── JwtConfigurationProperties.java
│       │   ├── LoginUserDetailsService.java
│       │   ├── controller/                 # /token, /register
│       │   └── error/
│       ├── apivalidation/
│       └── clock/
│
└── test/ + integration/
    ├── domain/offer/
    │   ├── OfferFacadeTest.java
    │   └── InMemoryOfferRepository.java
    └── domain/user/
        ├── UserFacadeTest.java
        └── InMemoryUserRepository.java
```

---

## 📄 License

This project is for educational and portfolio purposes.

---

<div align="center">
  <sub>Built with ☕ by <a href="https://github.com/sizeeq">Jakub Makuch</a></sub>
</div>
