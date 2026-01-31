# HCL\_Tech

Hackathon



E-Wallet README.md



Wallets table

&nbsp;   id BIGSERIAL PRIMARY KEY,

&nbsp;   user\_id BIGINT NOT NULL UNIQUE,       -- Links to User Service (Logical FK)

&nbsp;   balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000, -- 19 digits, 4 decimal places

&nbsp;   currency VARCHAR(3) NOT NULL DEFAULT 'INR',

&nbsp;   status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE, FROZEN, CLOSED

&nbsp;   version BIGINT NOT NULL DEFAULT 0,    -- 🔒 CRITICAL: For Optimistic Locking

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   updated\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP



-- Index for fast lookup by user

CREATE INDEX idx\_wallets\_user\_id ON wallets(user\_id);





wallet\_entries table

&nbsp;   id BIGSERIAL PRIMARY KEY,

&nbsp;   wallet\_id BIGINT NOT NULL,            -- FK to wallets table

&nbsp;   amount DECIMAL(19, 4) NOT NULL,       -- Transaction amount

&nbsp;   operation\_type VARCHAR(10) NOT NULL,  -- 'DEBIT' or 'CREDIT'

&nbsp;   transaction\_id VARCHAR(64) NOT NULL,  -- Global ID from Payment Service (for idempotency)

&nbsp;   description VARCHAR(255),             -- e.g., "Purchase at Starbucks"

&nbsp;   balance\_after DECIMAL(19, 4) NOT NULL,-- Snapshot of balance after this movement

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   

&nbsp;   CONSTRAINT fk\_wallet FOREIGN KEY (wallet\_id) REFERENCES wallets(id)



-- Index for generating user statements quickly

CREATE INDEX idx\_entries\_wallet\_id ON wallet\_entries(wallet\_id);

-- Index to check if a transaction was already processed (Idempotency)

CREATE UNIQUE INDEX idx\_entries\_txn\_id ON wallet\_entries(transaction\_id);







Base URL: `/api/wallets`



1\. Create Wallet

Initialize a new wallet for a user.

\*\*POST\*\* `/`

\*\*Query Param:\*\* `userId` (Long)

\*\*Response:\*\* `200 OK`



2\. Get Balance

Retrieve current balance

\*\*GET\*\* `/{userId}`

\*\*Response:\*\* 

{

&nbsp;     "balance": 150.00,

&nbsp;     "currency": "INR"

}



3\. Debit (Withdraw/Pay)

Deduct funds from a wallet.

\*\*POST\*\* `/{userId}/debit`

\*\*Body:\*\*

&nbsp;   {

&nbsp;     "amount": 50.00,

&nbsp;     "transactionId": "txn\_Id"

&nbsp;   }



4\. Credit (Deposit)

Add funds to a wallet.

\*\*POST\*\* `/{userId}/credit`

\*\*Body:\*\*

&nbsp;   {

&nbsp;     "amount": 100.00,

&nbsp;     "transactionId": "txn\_Id"

&nbsp;   }

