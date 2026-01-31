package com.hcl.ewallet.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.ewallet.payment.entity.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByMerchantId(String merchantId);

    Optional<Merchant> findByTransactionRef(String transactionRef);

    boolean existsByMerchantId(String merchantId);

    boolean existsByTransactionRef(String transactionRef);
}
