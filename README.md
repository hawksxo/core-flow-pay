# 💳 Core Flow Pay — Clean Architecture Payment System

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 4.x](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen.svg)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2F%20Hexagonal-blue.svg)
![Build](https://img.shields.io/badge/Build-Passing-success.svg)

`core-flow-pay` es un microservicio backend de procesamiento de suscripciones y pagos diseñado bajo los principios estrictos de **Clean Architecture (Hexagonal / Puertos y Adaptadores)**.

El objetivo principal de este proyecto es mantener el **Modelo de Dominio 100% puro**, completamente desacoplado de dependencias de frameworks (Spring), persistencia (JPA/Hibernate) o infraestructura HTTP.

---

## 🏛️ Arquitectura & Capas

El sistema sigue la **Regla de Dependencia Inviolable**: las capas externas conocen a las internas, pero el Dominio jamás conoce el mundo exterior.

```
                  ┌────────────────────────────────────────┐
                  │           INFRASTRUCTURE               │
                  │  (Spring Controllers, JPA, Adapters)   │
                  └──────────────────┬─────────────────────┘
                                     │
                                     ▼
                  ┌────────────────────────────────────────┐
                  │             APPLICATION                │
                  │       (Use Cases, Commands/DTOs)       │
                  └──────────────────┬─────────────────────┘
                                     │
                                     ▼
                  ┌────────────────────────────────────────┐
                  │               DOMAIN                   │
                  │   (Entities, Ports, Domain Exceptions) │
                  └────────────────────────────────────────┘
```

---

## 🔁 Flujo de Desarrollo (11 Pasos)

1. **`domain/model/`** — Entidad pura de negocio (`Subscription.java`).
2. **`domain/repository/`** — Puerto/Interfaz de repositorio (`SubscriptionRepository.java`).
3. **`domain/exception/`** — Excepciones explícitas de reglas de negocio (`SubscriptionAlreadyActiveException.java`).
4. **`application/dto/`** — Comandos de entrada inmutables (`CreateSubscriptionCommand.java`).
5. **`application/usecase/`** — Lógica de orquestación pura (`CreateSubscriptionUseCase.java`, `ActivateSubscriptionUseCase.java`, `CancelSubscriptionUseCase.java`).
6. **`infrastructure/persistence/entity/`** — Entidad JPA ORM (`SubscriptionEntity.java`).
7. **`infrastructure/persistence/repository/`** — Interfaz de Spring Data JPA (`SpringDataSubscriptionRepository.java`).
8. **`infrastructure/persistence/adapter/`** — Adaptador de persistencia (`JpaSubscriptionRepositoryAdapter.java`).
9. **`infrastructure/config/`** — Configuración explícita de Beans (`ApplicationConfig.java`).
10. **`infrastructure/web/exception/`** — Manejador global de excepciones HTTP (`GlobalExceptionHandler.java`).
11. **`infrastructure/web/controller/`** — Controlador REST API HTTP (`SubscriptionController.java`).

---

## 🚀 Endpoints de la API REST

| Método | Endpoint | Descripción | Estado Exitoso |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/subscriptions` | Crear una nueva suscripción | `201 Created` |
| `PATCH` | `/api/v1/subscriptions/{id}/activate` | Activar una suscripción existente | `200 OK` |
| `PATCH` | `/api/v1/subscriptions/{id}/cancel` | Cancelar una suscripción existente | `200 OK` |

---

## 🛠️ Tecnologías Utilizadas

* **Java 21 (LTS)**
* **Spring Boot 4.x** (Web, Data JPA)
* **H2 Database** (In-Memory para desarrollo y pruebas)
* **JUnit 5 & Mockito** (Pruebas unitarias de casos de uso)
* **Maven Wrapper**

---

## 💻 Pruebas & Ejecución

### Ejecutar las pruebas unitarias:
```bash
./mvnw clean test
```

### Iniciar el servidor local:
```bash
./mvnw spring-boot:run
```

La API estará escuchando en `http://localhost:8080`.
