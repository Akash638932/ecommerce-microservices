
# E-Commerce Microservices Backend

A backend-only **E-Commerce Microservices Application** developed using **Java and Spring Boot**.

## 🚀 Technologies

* Java
* Spring Boot
* Spring Cloud
* Spring Data JPA
* Spring Security
* Spring Cloud Gateway
* Netflix Eureka
* OpenFeign
* REST API
* MySQL
* Maven

## 🏗️ Microservices

* **Eureka Server** – Service discovery and registration
* **API Gateway** – Centralized API routing
* **Auth Service** – User registration and authentication
* **Product Service** – Product management
* **Cart Service** – Shopping cart management
* **Order Service** – Order management

## 🔄 Architecture

```text
Client
   ↓
API Gateway
   ↓
Eureka Service Discovery
   ↓
--------------------------------
| Auth Service                 |
| Product Service              |
| Cart Service                 |
| Order Service                |
--------------------------------
   ↓
MySQL Database
```

## ✨ Features

* Microservices architecture
* Service discovery using Eureka
* API Gateway
* RESTful APIs
* Authentication and authorization
* Product management
* Cart management
* Order management
* Inter-service communication using OpenFeign
* MySQL database integration
* Spring Data JPA

## 📁 Project Structure

```text
ecommerce-microservices/
│
├── eureka-server/
├── api-gateway/
├── auth-service/
├── product-service/
├── cart-service/
└── order-service/
```

## ▶️ How to Run

1. Clone the repository.
2. Start the **Eureka Server**.
3. Start the **API Gateway**.
4. Start the **Auth Service**.
5. Start the **Product Service**.
6. Start the **Cart Service**.
7. Start the **Order Service**.
8. Configure MySQL database details in each service's `application.properties`.

## 👨‍💻 Author

**Akash Kumar**
