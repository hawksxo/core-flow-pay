# 💳 Core Flow Pay — Subscription Module

Un microservicio backend modular desarrollado en **Java 21** y **Spring Boot 4.x**, diseñado bajo los principios de **Clean Architecture**, **Domain-Driven Design (DDD)** y **Package-by-Feature (Modulith)**.

El objetivo principal de este proyecto es mantener el **Modelo de Dominio 100% puro**, completamente desacoplado de dependencias de frameworks (Spring), persistencia (JPA/Hibernate) o infraestructura HTTP.

---

## 🏛️ Arquitectura Modular (Package-by-Feature)

El proyecto sigue una estructura orientada a características funcionales (`subscription`), garantizando un alto grado de encapsulamiento y modularidad:

```text
com.hawksxo.core_flow_pay/
└── subscription/
    ├── api/                   <-- REST Controllers, DTOs & Custom Exception Handlers
    ├── application/           <-- Casos de Uso (Orquestación & Inyección de Reloj/Clock)
    ├── config/                <-- Inversión de Control & Configuración de Spring Beans
    ├── domain/                <-- Entidad Pura (Instant, Factory Methods), Excepciones & Puertos
    └── infrastructure/        <-- Adaptadores JPA & Entidades de Persistencia ORM
```

---

## 🔁 Flujo de Desarrollo del Módulo

1. **`subscription/domain/`** — Entidad pura de negocio (`Subscription.java`) con sellado mediante Factory Methods (`create`, `reconstruct`), timestamps en UTC (`java.time.Instant`) y validación estricta de máquina de estados.
2. **`subscription/domain/`** — Puertos/Interfaces de repositorio (`SubscriptionRepository.java`) e Excepciones de negocio (`SubscriptionNotFoundException.java`, `SubscriptionInvalidStateException.java`, `DuplicateIdempotencyKeyException.java`).
3. **`subscription/application/`** — Casos de uso inmutables y desacoplados (`CreateSubscriptionUseCase.java`, `ActivateSubscriptionUseCase.java`, `CancelSubscriptionUseCase.java`).
4. **`subscription/infrastructure/persistence/`** — Adaptadores JPA e integración ORM (`SubscriptionEntity.java`, `JpaSubscriptionRepositoryAdapter.java`).
5. **`subscription/api/`** — Endpoints de la API REST (`SubscriptionController.java`) y mapeo estandarizado de errores HTTP (`SubscriptionExceptionHandler.java`).

---

## 🚀 Endpoints de la API REST

| Método | Endpoint | Descripción | Header Requerido | Estado Exitoso |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/subscriptions` | Crear una nueva suscripción | `Idempotency-Key` | `201 Created` |
| `POST` | `/api/v1/subscriptions/{id}/activate` | Activar una suscripción pendiente | — | `200 OK` |
| `POST` | `/api/v1/subscriptions/{id}/cancel` | Cancelar una suscripción existente | — | `200 OK` |

---

## 🛠️ Tecnologías Utilizadas

* **Java 21 (LTS)**
* **Spring Boot 4.x** (Web, Data JPA)
* **H2 Database** (Base de datos In-Memory para desarrollo y pruebas)
* **JUnit 5, Mockito & MockMvc** (Suite completa de pruebas unitarias e integración)
* **Maven Wrapper**

---

## 💻 Pruebas & Ejecución

### Ejecutar la suite completa de pruebas:
```bash
./mvnw clean test
```

### Iniciar el servidor local:
```bash
./mvnw spring-boot:run
```

La API estará escuchando en `http://localhost:8080`.
