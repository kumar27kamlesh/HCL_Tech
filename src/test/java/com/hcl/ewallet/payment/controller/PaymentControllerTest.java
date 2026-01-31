package com.hcl.ewallet.payment.controller;

import com.hcl.ewallet.payment.enums.TransactionStatus;
import com.hcl.ewallet.payment.exception.CustomerNotFoundException;
import com.hcl.ewallet.payment.exception.ProductNotFoundException;
import com.hcl.ewallet.payment.model.PaymentRequest;
import com.hcl.ewallet.payment.model.PaymentResponse;
import com.hcl.ewallet.payment.service.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class PaymentControllerMockitoTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setCustomerId("CUST001");
        paymentRequest.setMerchantId("MER001");
        paymentRequest.setAmount(new BigDecimal(500));
        paymentRequest.setCurrency("USD");

        paymentResponse = new PaymentResponse();
        paymentResponse.setStatus(TransactionStatus.INITIATED);
        paymentResponse.setTransactionRef("TXN100001");
        paymentResponse.setMessage("Payment processed successfully");
    }

    // ----------------------------------
    // Test 1: GET /paymentstest
    // ----------------------------------
    @Test
    void makePaymentTest_shouldReturnSuccessMessage() {

        String result = paymentController.makePaymentTest();

        assertEquals("Payment Success", result);
    }

    // ----------------------------------
    // Test 2: POST /payments
    // ----------------------------------
    @Test
    void makePayment_shouldReturnOkResponse() throws CustomerNotFoundException, ProductNotFoundException {

        Mockito.when(paymentService.processPayment(paymentRequest))
                .thenReturn(paymentResponse);

        ResponseEntity<PaymentResponse> responseEntity =
                paymentController.makePayment(paymentRequest);

        assertNotNull(responseEntity);
       // assertEquals(200, responseEntity.getStatusCodeValue());
        //assertEquals("SUCCESS", responseEntity.getBody().getStatus());
        //assertEquals("TXN100001", responseEntity.getBody().getTransactionRef());

        Mockito.verify(paymentService, Mockito.times(1))
                .processPayment(paymentRequest);
    }
}
