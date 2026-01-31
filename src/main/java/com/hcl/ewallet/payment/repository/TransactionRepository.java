package com.hcl.ewallet.payment.repository;

import com.hcl.ewallet.payment.entity.Transaction;
import com.hcl.ewallet.payment.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find transaction by unique reference
    Optional<Transaction> findByTransactionRef(String transactionRef);

    // Get all transactions for a customer
    List<Transaction> findByCustomerId(String customerId);

    // Get all transactions for a merchant
    List<Transaction> findByMerchantId(String merchantId);

    // Filter transactions by status
    List<Transaction> findByStatus(TransactionStatus status);

    // Customer transactions by status
    List<Transaction> findByCustomerIdAndStatus(
            String customerId,
            TransactionStatus status
    );

    // Merchant transactions by status
    List<Transaction> findByMerchantIdAndStatus(
            String merchantId,
            TransactionStatus status
    );
}
