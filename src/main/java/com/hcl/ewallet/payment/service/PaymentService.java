package com.hcl.ewallet.payment.service;

import org.springframework.stereotype.Service;

import com.hcl.ewallet.payment.exception.CustomerNotFoundException;
import com.hcl.ewallet.payment.exception.ProductNotFoundException;
import com.hcl.ewallet.payment.model.PaymentRequest;
import com.hcl.ewallet.payment.model.PaymentResponse;


public interface PaymentService {

	PaymentResponse processPayment(PaymentRequest paymentRequest) throws CustomerNotFoundException, ProductNotFoundException;

}
