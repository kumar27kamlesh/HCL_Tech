package com.hcl.ewallet.merchant.dto;

import java.math.BigDecimal;

public class CreditRequest {

    private BigDecimal amount;
    private String customerId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
