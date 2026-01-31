package com.hcl.ewallet.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hcl.ewallet.payment.entity.Customer;
import com.hcl.ewallet.payment.entity.Product;
import com.hcl.ewallet.payment.entity.Transaction;
import com.hcl.ewallet.payment.enums.TransactionStatus;
import com.hcl.ewallet.payment.exception.CustomerNotFoundException;
import com.hcl.ewallet.payment.exception.ProductNotFoundException;
import com.hcl.ewallet.payment.model.PaymentRequest;
import com.hcl.ewallet.payment.model.PaymentResponse;
import com.hcl.ewallet.payment.repository.CustomerRepository;
import com.hcl.ewallet.payment.repository.ProductRepository;
import com.hcl.ewallet.payment.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setCustomerId("CUST001");
        paymentRequest.setProductId("PROD001");
        paymentRequest.setAmount(new BigDecimal("100.00"));

        customer = new Customer();
        customer.setCustomerId("CUST001");
        customer.setStatus("ACTIVE");
        customer.setAmount(new BigDecimal("500.00"));
        customer.setWalletAmount(new BigDecimal("10.00"));

        product = new Product();
        product.setProductId("PROD001");
        product.setStatus("ACTIVE");
        product.setCurrency("USD");
    }

    // -------------------------------------------------
    // 1️⃣ Success case
    // -------------------------------------------------
    @Test
    void processPayment_shouldReturnSuccessResponse() 
            throws CustomerNotFoundException, ProductNotFoundException {

        when(customerRepository.findByCustomerId("CUST001"))
                .thenReturn(Optional.of(customer));

        when(productRepository.findByProductId("PROD001"))
                .thenReturn(Optional.of(product));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response =
                paymentService.processPayment(paymentRequest);

        assertNotNull(response);
        assertNotNull(response.getTransactionRef());
        assertEquals(TransactionStatus.INITIATED, response.getStatus());
        assertEquals("SUCCESS", response.getMessage());

        verify(customerRepository, times(1))
                .findByCustomerId("CUST001");
        verify(productRepository, times(1))
                .findByProductId("PROD001");
        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }

    // -------------------------------------------------
    // 2️⃣ Customer not found
    // -------------------------------------------------
    @Test
    void processPayment_shouldThrowCustomerNotFoundException() {

        when(customerRepository.findByCustomerId("CUST001"))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                paymentService.processPayment(paymentRequest));

        verify(customerRepository, times(1))
                .findByCustomerId("CUST001");
        verifyNoInteractions(productRepository, transactionRepository);
    }

    // -------------------------------------------------
    // 3️⃣ Product not found
    // -------------------------------------------------
    @Test
    void processPayment_shouldThrowProductNotFoundException()
            throws CustomerNotFoundException {

        when(customerRepository.findByCustomerId("CUST001"))
                .thenReturn(Optional.of(customer));

        when(productRepository.findByProductId("PROD001"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                paymentService.processPayment(paymentRequest));

        verify(productRepository, times(1))
                .findByProductId("PROD001");
        verify(transactionRepository, never()).save(any());
    }

    // -------------------------------------------------
    // 4️⃣ Inactive customer
    // -------------------------------------------------
    @Test
    void processPayment_shouldThrowException_whenCustomerInactive() {

        customer.setStatus("INACTIVE");

        when(customerRepository.findByCustomerId("CUST001"))
                .thenReturn(Optional.of(customer));

        assertThrows(CustomerNotFoundException.class, () ->
                paymentService.processPayment(paymentRequest));
    }

    // -------------------------------------------------
    // 5️⃣ Inactive product
    // -------------------------------------------------
    @Test
    void processPayment_shouldThrowException_whenProductInactive()
            throws CustomerNotFoundException {

        product.setStatus("INACTIVE");

        when(customerRepository.findByCustomerId("CUST001"))
                .thenReturn(Optional.of(customer));

        when(productRepository.findByProductId("PROD001"))
                .thenReturn(Optional.of(product));

        assertThrows(ProductNotFoundException.class, () ->
                paymentService.processPayment(paymentRequest));
    }
}
