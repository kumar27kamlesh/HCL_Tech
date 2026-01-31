# Merchant Service – E-Wallet Microservices System

## 📌 Overview

Merchant Service manages merchant accounts and processes payment credits as part
of the Saga-based transaction workflow.

It consumes wallet debit events from Wallet Service, credits merchant wallets
locally after deducting wallet fees, initiates settlement to merchant bank
accounts through a payment gateway, and supports compensating rollbacks in case
of failures.

The service primarily operates in an event-driven manner using Kafka with
minimal REST APIs.

---

## 🎯 Responsibilities

- Maintain merchant profile and wallet balance
- Consume wallet-debited events from Wallet Service
- Credit merchant wallet after customer wallet debit
- Deduct and collect wallet fees
- Initiate settlement to merchant bank via payment gateway
- Publish success/failure events
- Handle rollback (compensation)
- Provide merchant wallet query APIs

---

## 🏗️ Role in Architecture

Saga Flow:

Wallet Service → Merchant Service → Ledger Service

Merchant Service performs local account updates and triggers settlement without
blocking the main transaction flow.

---

## 📦 Technology Stack

- Spring Boot – Microservices framework
- Apache Kafka – Event-driven messaging for Saga orchestration
- PostgreSQL – Relational database for merchant data
- Swagger OpenAPI – API documentation and testing


---

## 🗄️ Database Design

### Merchant Table

| Column | Type | Description |
|-------|------|------------|
| merchant_id | VARCHAR (PK) | Unique merchant identifier |
| name | VARCHAR | Merchant name |
| status | VARCHAR | ACTIVE / INACTIVE |
| created_at | TIMESTAMP | Creation timestamp |

---

### Merchant Wallet Table

| Column | Type | Description |
|-------|------|------------|
| wallet_id | VARCHAR (PK) | Wallet identifier |
| merchant_id | VARCHAR (FK) | Linked merchant |
| balance | DECIMAL | Wallet balance |
| currency | VARCHAR | Currency |
| updated_at | TIMESTAMP | Last updated |

---

### Merchant Transaction Table

| Column | Type | Description |
|-------|------|------------|
| id | VARCHAR (PK) | Record ID |
| txn_id | VARCHAR | Global transaction ID |
| gross_amount | DECIMAL | Original payment amount |
| fee_deducted | DECIMAL | Wallet fee |
| net_amount | DECIMAL | Amount credited |
| status | VARCHAR | SUCCESS / ROLLED_BACK |
| created_at | TIMESTAMP | Timestamp |

---

### Settlement Table

| Column | Type | Description |
|-------|------|------------|
| settlement_id | VARCHAR (PK) | Settlement reference |
| txn_id | VARCHAR | Transaction reference |
| merchant_id | VARCHAR | Merchant |
| amount | DECIMAL | Settlement amount |
| bank_reference | VARCHAR | Gateway reference |
| status | VARCHAR | PENDING / COMPLETED / FAILED |
| created_at | TIMESTAMP | Timestamp |

---

## 💸 Wallet Fee Logic

Wallet fee is applied before crediting merchant wallet.

### Example:

Customer Payment: 1000  
Wallet Fee: 20  
Merchant Wallet Credit: 980

Fee is stored in ledger and can be transferred to platform revenue account.

---


# Merchant Service – E-Wallet Microservices System

## 🌐 REST API DETAILS

### 1️⃣ Create Merchant

POST /api/merchants

Request:
```json
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
