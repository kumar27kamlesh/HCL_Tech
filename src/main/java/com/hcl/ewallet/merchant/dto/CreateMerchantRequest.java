package com.hcl.ewallet.merchant.dto;

public class CreateMerchantRequest {

    private String name;
    private String currency;

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
