# E-Commerce Microservices Backend

## Overview

This project is a microservices-based backend for an e-commerce platform, developed using Spring Boot. It includes various services such as Product, Inventory, Order, and Notification, all designed to provide a robust and scalable solution for e-commerce operations.

## Key Features

### Microservices Architecture
- **Product Service:** Manages product information.
- **Inventory Service:** Keeps track of product stock levels.
- **Order Service:** Handles customer orders and order processing.
- **Notification Service:** Sends notifications related to orders and inventory.

### Service Discovery and API Gateway
- **Netflix Eureka:** Implemented for service discovery, allowing efficient communication between microservices.
- **Spring Cloud Gateway:** Used as an API Gateway for routing and load balancing, ensuring efficient request handling.

### Security and Resilience
- **Keycloak:** Secured microservices with Keycloak for identity and access management.
- **Circuit Breaker:** Implemented using Spring Cloud Circuit Breaker to provide resilience and prevent cascading failures.

### Event-Driven Architecture
- **Apache Kafka:** Utilized for event-driven communication between microservices, enabling asynchronous processing and improving system scalability.

### Containerization and Monitoring
- **Docker:** Dockerized all microservices for consistent deployment across different environments.
- **Prometheus and Grafana:** Set up comprehensive monitoring and observability using Prometheus for metrics collection and Grafana for visualization, ensuring seamless performance tracking.

## Technologies Used

- **Spring Boot**
- **Spring Cloud Netflix (Eureka)**
- **Spring Cloud Gateway**
- **Spring Cloud Circuit Breaker**
- **Keycloak**
- **Apache Kafka**
- **Docker**
- **Prometheus**
- **Grafana**

## Getting Started

### Prerequisites

Ensure you have the following installed:
- Java 11 or higher
- Docker
- Docker Compose
- Apache Kafka
- Prometheus
- Grafana

### Running the Application

#### Using Docker

1. **Build the applications and create the Docker images locally:**
   ```sh
   mvn clean package -DskipTests

2. **Start the applications**
   ```sh
   docker-compose up -d

#### Without Docker
1. **Build the applications:**
   ```sh
   mvn clean verify -DskipTests
2. **Run the applications:**
   
   *Navigate to each service folder and run*
   ```sh
   mvn spring-boot:run

### Accessing Services
1. Eureka Dashboard: http://localhost:8761
2. API Gateway: http://localhost:8080
3. Prometheus: http://localhost:9090
4. Grafana: http://localhost:3000

### Example Endpoints
1. Product Service: http://localhost:8080/api/products
2. Inventory Service: http://localhost:8080/api/inventory
3. Order Service: http://localhost:8080/api/orders
4. Notification Service: http://localhost:8080/api/notifications