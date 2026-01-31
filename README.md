# HCL_Tech
Hackathon
# 💳 E-Wallet Payment System – Microservices (Spring Boot)

## 📌 Overview
This repository contains a **production-ready E-Wallet Payment System** implemented using **Spring Boot microservices**.  
The system enables users to make wallet-based payments to merchants while ensuring **strong consistency, auditability, and fault tolerance**.

The design follows **real-world fintech architecture principles**, using:
- Database-per-service
- Saga-based transaction orchestration
- Event-driven ledger and notifications
- Clear service boundaries

---

## 🧩 Business Requirements Covered
- Display customer details, wallet account & balance
- Initiate purchase with product & merchant details
- Validate sufficient wallet balance and currency
- Deduct wallet amount and credit merchant
- Collect wallet fee
- Maintain transaction ledger and audit trail
- Ensure transaction consistency and compensation
- Notify users and merchants of payment status

---

## 🏗️ System Architecture

### Microservices
| Service | Responsibility |
|------|----------------|
| API Gateway | Single entry point, routing |
| User Service | User profile & status |
| Wallet Service | Wallet balance (source of truth) |
| Merchant Service | Merchant account & settlement |
| Payment Service | Payment orchestration (Saga) |
| Ledger Service | Immutable transaction ledger |
| Notification Service | Payment notifications |

### Architectural Principles
- **Strong consistency for money movement**
- **Saga orchestration** for distributed transactions
- **Synchronous REST** for balance-critical operations
- **Asynchronous Kafka events** for audit & notifications
- **No shared databases**

---

## 🔁 High-Level Payment Flow

1. Client initiates payment
2. Payment Service validates request
3. Wallet Service validates & debits wallet
4. Merchant Service credits merchant (after fee)
5. Payment Service publishes success event
6. Ledger Service records audit entries
7. Notification Service sends alerts

Failures are handled using **compensating actions**.

---

## 🧪 Technology Stack
- Java 17
- Spring Boot 3.x
- Spring Web & Spring Data JPA
- Spring Cloud Gateway
- MySQL
- Apache Kafka
- Springdoc OpenAPI (Swagger)
- Docker & Docker Compose

---

## 📦 Repository Structure

