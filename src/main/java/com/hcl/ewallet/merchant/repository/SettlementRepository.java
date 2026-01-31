package com.hcl.ewallet.merchant.repository;

import com.hcl.ewallet.merchant.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, String> {
}
