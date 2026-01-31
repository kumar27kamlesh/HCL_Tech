package com.hcl.ewallet.merchant.repository;

import com.hcl.ewallet.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
}
