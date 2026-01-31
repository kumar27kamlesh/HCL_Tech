package com.hcl.ewallet.payment.model;

import lombok.*;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;


public class PaymentRequest {
	
	@NotBlank(message = "customerId should not empty or null")
    private String customerId;
	
	@NotBlank(message = "merchantId should not empty or null")
    private String merchantId;
	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@NotBlank(message = "productId should not empty or null")
    private String productId;
	
	@NotBlank(message = "productName should not empty or null")
    private String productName;
	
	//@NotBlank(message = "amount should not be lessthan 100")
    private BigDecimal amount;
	
	@NotBlank(message = "currency should not empty or null")
    private String currency;
}
