package com.hcl.ewallet.merchant.repository;

import com.hcl.ewallet.merchant.entity.MerchantWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantWalletRepository
        extends JpaRepository<MerchantWallet, String> {

    Optional<MerchantWallet> findByMerchantId(String merchantId);
}
