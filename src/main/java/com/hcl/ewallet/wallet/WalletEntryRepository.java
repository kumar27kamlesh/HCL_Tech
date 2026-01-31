package com.hcl.ewallet.wallet;

import com.hcl.ewallet.wallet.WalletEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WalletEntryRepository extends JpaRepository<WalletEntry, Long> {
    
    boolean existsByTransactionId(String transactionId);

    @Query(value = "SELECT MAX(CAST(SUBSTRING(transaction_id, 4) AS UNSIGNED)) FROM wallet_entries", nativeQuery = true)
    Long findMaxTransactionId();
}