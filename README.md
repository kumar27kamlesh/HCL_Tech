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
## 📌 Overview
======================User Service – E-Wallet Microservices System=============
The **User Service** is responsible for managing **customer identity, profile information, and eligibility status** in the E-Wallet system.

It acts as the **single source of truth for user data** and is used by other services (such as Payment Service) to validate whether a user is allowed to perform wallet transactions.

❌ This service does NOT handle wallet balances or financial transactions.

---

## 🎯 Responsibilities
- Create and manage user profiles
- Maintain user status (ACTIVE / BLOCKED)
- Expose user details for internal validation
- Ensure data integrity and uniqueness

---

## 🏗️ High-Level Architecture

UserController
↓
UserService
↓
UserRepository
↓
PostgreSQL (user_db)


- Stateless REST service
- Database-per-service
- Synchronous communication only

---

## 🗃️ Database Design

### 📄 Table: `users`

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(150) NOT NULL UNIQUE,
    mobile_number VARCHAR(15) UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

Base Path:- /api/users
POST /api/users
GET /api/users/{userId}
GET /api/users/email/{email}
PATCH /api/users/{userId}/status
GET /api/users/{userId}/validate



======================Merchant Service – E-Wallet Microservices System=============
📌 Overview

Merchant Service manages merchant accounts and processes payment credits as part of the Saga-based transaction workflow.

It consumes wallet debit events from Wallet Service, credits merchant wallets locally after deducting wallet fees, initiates settlement to merchant bank accounts through a payment gateway, and supports compensating rollbacks in case of failures.

The service primarily operates in an event-driven manner using Kafka with minimal REST APIs.
🎯 Responsibilities

    Maintain merchant profile and wallet balance
    Consume wallet-debited events from Wallet Service
    Credit merchant wallet after customer wallet debit
    Deduct and collect wallet fees
    Initiate settlement to merchant bank via payment gateway
    Publish success/failure events
    Handle rollback (compensation)
    Provide merchant wallet query APIs

🏗️ Role in Architecture

Saga Flow:

Wallet Service → Merchant Service → Ledger Service

Merchant Service performs local account updates and triggers settlement without blocking the main transaction flow.
📦 Technology Stack

    Spring Boot – Microservices framework
    Apache Kafka – Event-driven messaging for Saga orchestration
    PostgreSQL – Relational database for merchant data
    Swagger OpenAPI – API documentation and testing

🗄️ Database Design
Merchant Table
Column 	Type 	Description
merchant_id 	VARCHAR (PK) 	Unique merchant identifier
name 	VARCHAR 	Merchant name
status 	VARCHAR 	ACTIVE / INACTIVE
created_at 	TIMESTAMP 	Creation timestamp
Merchant Wallet Table
Column 	Type 	Description
wallet_id 	VARCHAR (PK) 	Wallet identifier
merchant_id 	VARCHAR (FK) 	Linked merchant
balance 	DECIMAL 	Wallet balance
currency 	VARCHAR 	Currency
updated_at 	TIMESTAMP 	Last updated
Merchant Transaction Table
Column 	Type 	Description
id 	VARCHAR (PK) 	Record ID
txn_id 	VARCHAR 	Global transaction ID
gross_amount 	DECIMAL 	Original payment amount
fee_deducted 	DECIMAL 	Wallet fee
net_amount 	DECIMAL 	Amount credited
status 	VARCHAR 	SUCCESS / ROLLED_BACK
created_at 	TIMESTAMP 	Timestamp
Settlement Table
Column 	Type 	Description
settlement_id 	VARCHAR (PK) 	Settlement reference
txn_id 	VARCHAR 	Transaction reference
merchant_id 	VARCHAR 	Merchant
amount 	DECIMAL 	Settlement amount
bank_reference 	VARCHAR 	Gateway reference
status 	VARCHAR 	PENDING / COMPLETED / FAILED
created_at 	TIMESTAMP 	Timestamp
💸 Wallet Fee Logic

Wallet fee is applied before crediting merchant wallet.
Example:

Customer Payment: 1000
Wallet Fee: 20
Merchant Wallet Credit: 980

Fee is stored in ledger and can be transferred to platform revenue account.
Merchant Service – E-Wallet Microservices System
🌐 REST API DETAILS
1️⃣ Create Merchant

POST /api/merchants

Request:

{
  "name": "Amazon Store",
  "currency": "INR"
}

Response (201):
```json
{
  "merchantId": "M1",
  "status": "CREATED"
}

### 1️⃣ Get Merchant Details

GET /api/merchants/{merchantId}

Response (200):
```json
{
  "merchantId": "M1",
  "name": "Amazon Store",
  "status": "ACTIVE"
}

### 1️⃣ Get Merchant Wallet

GET /api/merchants/{merchantId}/wallet

Response (200):
```json
{
  "merchantId": "M1",
  "balance": 9800,
  "currency": "INR",
  "status": "ACTIVE"
}

### 1️⃣ Get Merchant Transactions

GET /api/merchants/{merchantId}/transactions

Response (200):
```json
[
  {
    "txnId": "TX1001",
    "grossAmount": 1000,
    "fee": 20,
    "netAmount": 980,
    "status": "SUCCESS"
  }
]

### 1️⃣ Get Settlement Status

GET /api/settlements/{settlementId}

Response (200):
```json
{
  "settlementId": "S1001",
  "txnId": "TX1001",
  "amount": 980,
  "status": "COMPLETED"
}





## 📩 Kafka Events

### 🔔 Consumes

#### Topic: `wallet-debited`

```json
{
  "txnId": "TX1001",
  "merchantId": "M1",
  "amount": 1000,
  "currency": "INR"
}

### 🔔 Publishes (Success)

#### Topic: `merchant-credited`

```json
{
  "txnId": "TX1001",
  "merchantId": "M1",
  "grossAmount": 1000,
  "fee": 20,
  "creditedAmount": 980,
  "status": "SUCCESS"
}

### 🔔 Publishes (FAILURE)

#### Topic: `merchant-credit-failed`

```json
{
  "txnId": "TX1001",
  "merchantId": "M1",
  "reason": "PROCESSING_ERROR"
}

### 🔔 Consumes (ROLLBACK)

#### Topic: `merchant-rollback`

```json
{
  "txnId": "TX1001",
  "merchantId": "M1",
  "creditedAmount": 980
}

### 🔔 Publishes (SETTLEMENT INITIATION)

#### Topic: `settlement-initiated`

```json

{
  "txnId": "TX1001",
  "merchantId": "M1",
  "amount": 980
}

===============================================Wallet Microservices – E-Wallet System=============

Wallets table

  id BIGSERIAL PRIMARY KEY,

  user_id BIGINT NOT NULL UNIQUE, -- Links to User Service (Logical FK)

  balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000, -- 19 digits, 4 decimal places

  currency VARCHAR(3) NOT NULL DEFAULT 'INR',

  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, FROZEN, CLOSED

  version BIGINT NOT NULL DEFAULT 0, -- 🔒 CRITICAL: For Optimistic Locking

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

-- Index for fast lookup by user

CREATE INDEX idx_wallets_user_id ON wallets(user_id);

wallet_entries table

  id BIGSERIAL PRIMARY KEY,

  wallet_id BIGINT NOT NULL, -- FK to wallets table

  amount DECIMAL(19, 4) NOT NULL, -- Transaction amount

  operation_type VARCHAR(10) NOT NULL, -- 'DEBIT' or 'CREDIT'

  transaction_id VARCHAR(64) NOT NULL, -- Global ID from Payment Service (for idempotency)

  description VARCHAR(255), -- e.g., "Purchase at Starbucks"

  balance_after DECIMAL(19, 4) NOT NULL,-- Snapshot of balance after this movement

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

 

  CONSTRAINT fk_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)

-- Index for generating user statements quickly

CREATE INDEX idx_entries_wallet_id ON wallet_entries(wallet_id);

-- Index to check if a transaction was already processed (Idempotency)

CREATE UNIQUE INDEX idx_entries_txn_id ON wallet_entries(transaction_id);

Base URL: /api/wallets

1.⁠ ⁠Create Wallet

Initialize a new wallet for a user.

*POST* /

*Query Param:* userId (Long)

*Response:* 200 OK

2.⁠ ⁠Get Balance

Retrieve current balance

*GET* /{userId}

*Response:*

{

  "balance": 150.00,

  "currency": "INR"

}

3.⁠ ⁠Debit (Withdraw/Pay)

Deduct funds from a wallet.

*POST* /{userId}/debit

*Body:*

  {

  "amount": 50.00,

  "transactionId": "txn_Id"

  }

4.⁠ ⁠Credit (Deposit)

Add funds to a wallet.

*POST* /{userId}/credit

*Body:*

  {

  "amount": 100.00,

  "transactionId": "txn_Id"

  }
  ==================Payment Service – E-Wallet System=============
  ## High-Level Responsibilities
•⁠  ⁠Validate payment requests
•⁠  ⁠Deduct amount from customer wallet
•⁠  ⁠Credit amount to merchant account (logical credit)
•⁠  ⁠Apply wallet fees
•⁠  ⁠Maintain transaction ledger
•⁠  ⁠Notify merchant on successful payment
•⁠  ⁠Handle failures and compensations

-----------------------------------------
Transactions (TABLE - transactions)
----------------------------------
private Long id;
private String customerId;
private String merchantId;
private BigDecimal amount;
private String currency;
private BigDecimal walletFee;
private TransactionStatus status;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;


TransactionStatus (TABLE - transaction_audit)
----------------------------
private String transactionRef;
private String action;
private String performedBy;
private LocalDateTime performedAt;


ENUM
----------------
INITIATED, PROCESSING, SUCCESS, FAILED


Payment Request:
 customerId,
 merchantId;
 productId;
 productName;
 amount;
 currency;

Payment Response:
 transactionRef;
 TransactionStatus;
 message;
 
  PUT /wallet-payment

==================Ledger Service – E-Wallet System=============
The Ledger Service maintains an immutable, append-only transaction ledger for all wallet payments.

It is used for:
    Audit & compliance
    Financial reconciliation
    Reporting & investigation

Ledger Service Responsibilities
    Record wallet debit entries
    Record merchant credit entries
    Record wallet fee entries
    Maintain double-entry accounting
    Provide read-only ledger APIs

    Table: ledger_entries
CREATE TABLE ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_type VARCHAR(30) NOT NULL,
    account_id BIGINT NOT NULL,
    debit DECIMAL(18,2) DEFAULT 0,
    credit DECIMAL(18,2) DEFAULT 0,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

Base URL: /api/ledger
GET /api/ledger/transactions/{transactionId}

==================Messaging Service – E-Wallet System=============