package com.hcl.ewallet.merchant.repository;

import com.hcl.ewallet.merchant.entity.MerchantTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantTransactionRepository
        extends JpaRepository<MerchantTransaction, String> {

    List<MerchantTransaction> findByMerchantId(String merchantId);
}
