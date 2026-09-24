# Core Flow Pay — Subscription Module

A modular backend microservice developed in **Java 21** and **Spring Boot 4.x**, designed according to the principles of **Clean Architecture**, **Domain-Driven Design (DDD)**, and **Package-by-Feature (Modulith)**..

The main objective of this project is to maintain the **100% pure Domain Model**, completely decoupled from framework dependencies (Spring), persistence (JPA/Hibernate) or HTTP infrastructure.

---

## Modular Architecture (Package-by-Feature)

The project follows a structure oriented around functional features (`subscription`), ensuring a high degree of encapsulation and modularity.:

```text
com.hawksxo.core_flow_pay/
└── subscription/
    ├── api/                   <-- REST Controllers, DTOs & Custom Exception Handlers
    ├── application/           <-- Use Cases (Orchestration & Clock Injection)
    ├── config/                <-- Inversion of Control and Spring Bean Configuration
    ├── domain/                <-- Pure Entity (Instant, Factory Methods), Exceptions & Ports
    └── infrastructure/        <-- JPA Adapters & ORM Persistence Entities
```

---

## Module Development Workflow

1. **`subscription/domain/`** — Pure business entity (`Subscription.java`) encapsulated via factory methods (`create`, `reconstruct`), with UTC timestamps (`java.time.Instant`) and strict state machine validation..
2. **`subscription/domain/`** — Repository ports/interfaces (`SubscriptionRepository.java`) and business exceptions (`SubscriptionNotFoundException.java`, `SubscriptionInvalidStateException.java`, `DuplicateIdempotencyKeyException.java`).
3. **`subscription/application/`** — Immutable and decoupled use cases (`CreateSubscriptionUseCase.java`, `ActivateSubscriptionUseCase.java`, `CancelSubscriptionUseCase.java`).
4. **`subscription/infrastructure/persistence/`** — JPA adapters and ORM integration (`SubscriptionEntity.java`, `JpaSubscriptionRepositoryAdapter.java`).
5. **`subscription/api/`** — REST API endpoints (`SubscriptionController.java`) and standardized HTTP error mapping (`SubscriptionExceptionHandler.java`).

---

## REST API Endpoints

| Method | Endpoint | Description | Required Header | Successful Status |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/subscriptions` | Create a new subscription | `Idempotency-Key` | `201 Created` |
| `POST` | `/api/v1/subscriptions/{id}/activate` | Activate a pending subscription | — | `200 OK` |
| `POST` | `/api/v1/subscriptions/{id}/cancel` | Cancel an existing subscription | — | `200 OK` |

---

## Technologies Used

* **Java 21 (LTS)**
* **Spring Boot 4.x** (Web, Data JPA)
* **H2 Database** (In-memory database for development and testing)
* **JUnit 5, Mockito & MockMvc** (Complete suite of unit and integration tests)
* **Maven Wrapper**

---

## Testing & Execution

### Run the complete test suite:
```bash
./mvnw clean test
```

### Start the local server:
```bash
./mvnw spring-boot:run
```

The API will be listening on `http://localhost:8080`.
