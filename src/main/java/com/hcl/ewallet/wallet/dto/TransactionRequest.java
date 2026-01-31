package com.hcl.ewallet.wallet.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private BigDecimal amount;
    
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}