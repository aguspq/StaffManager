# 🚀 StaffManager | Robust REST API for Organizational Management

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-24.0-blue?style=for-the-badge&logo=docker)

**StaffManager** is a backend architecture laboratory designed to manage the complexity of organizational structures. This project serves as a technical environment for implementing professional design patterns, focusing on **referential integrity**, **layer decoupling**, and **clean persistence logic**.

---

## 🏗️ Architectural Decisions

This project prioritizes a clean separation of concerns and robust dependency management:

* **Pattern Decoupling (DTO):** Strict separation between JPA persistence entities and Data Transfer Objects (DTO) to prevent domain leakage into the API layer.
* **Constructor Injection:** Use of constructor-based dependency injection to ensure service immutability and improve testability, moving away from field-level `@Autowired`.
* **Global Error Handling:** Centralized exception management via `@RestControllerAdvice`, ensuring that the API provides semantic, consistent, and controlled HTTP responses.

## 🔗 Advanced Persistence Management

The technical core of the system focuses on the orchestration of complex data relationships:

* **Defensive Entity Logic:** Implementation of internal helper methods (e.g., `addProject`) within entities to maintain synchronization in `@ManyToMany` bidirectional relationships.
* **Soft Delete Engine:** Logical deletion management via the `isActive` state flag. This preserves data for **auditing purposes** while keeping the active dataset clean for standard operations.
* **Atomic Operations:** Strategic use of `@Transactional` at the service layer to guarantee data consistency during multi-entity updates.

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Runtime** | Java 17 (Amazon Corretto) |
| **Framework** | Spring Boot 3.4.x |
| **Persistence** | Spring Data JPA / Hibernate |
| **Database** | PostgreSQL 15 |
| **Documentation** | OpenAPI 3 (Swagger UI) |
| **Infra** | Docker & Docker Compose |

## 🗺️ Technical Evolution

This project follows a continuous improvement path. Current development focuses:

- [ ] **MapStruct Integration:** Transitioning from manual mappers to automated code generation to eliminate boilerplate.
- [ ] **Advanced Domain Validation:** Implementing strict business rules within the service layer (e.g., workload limits and status-based assignments).
- [ ] **Security Layer:** Integrating Spring Security 6 with JWT for Role-Based Access Control (RBAC).
- [ ] **Performance Tuning:** Solving N+1 query issues through the implementation of `EntityGraphs` and specialized JPQL fetching.

## ⚙️ Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/aguspq/StaffManager.git](https://github.com/aguspq/StaffManager.git)
    ```
2.  **Spin up infrastructure (Database):**
    ```bash
    docker-compose up -d
    ```
3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
4.  **Explore the API:**
    Access `http://localhost:8080/swagger-ui/index.html` once the server is running.

---
*Developed with a focus on **SOLID** principles, **Clean Code**, and professional engineering standards.*
