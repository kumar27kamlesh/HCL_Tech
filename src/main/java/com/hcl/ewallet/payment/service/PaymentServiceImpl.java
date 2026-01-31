package com.hcl.ewallet.payment.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.ewallet.payment.entity.Customer;
import com.hcl.ewallet.payment.entity.Product;
import com.hcl.ewallet.payment.entity.Transaction;
import com.hcl.ewallet.payment.enums.MerchantCodes;
import com.hcl.ewallet.payment.enums.TransactionStatus;
import com.hcl.ewallet.payment.exception.CustomerNotFoundException;
import com.hcl.ewallet.payment.exception.ProductNotFoundException;
import com.hcl.ewallet.payment.model.PaymentRequest;
import com.hcl.ewallet.payment.model.PaymentResponse;
import com.hcl.ewallet.payment.repository.CustomerRepository;
import com.hcl.ewallet.payment.repository.ProductRepository;
import com.hcl.ewallet.payment.repository.TransactionRepository;

@Service
public class PaymentServiceImpl implements PaymentService {
	
	private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	private static final String CUSTOMER_STATUS = "ACTIVE";
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	
	
	
	@Override
	public PaymentResponse processPayment(PaymentRequest paymentRequest) throws CustomerNotFoundException, ProductNotFoundException {
		PaymentResponse paymentResponse = new PaymentResponse();
        log.info("Inside PaymentServiceImpl !!" +paymentRequest.toString());

		
		Customer customer = checkValidateCustomer(paymentRequest.getCustomerId());
		Product product = checkValidateProduct(paymentRequest.getProductId());
		
		Transaction transaction = createTransactionEntity(customer, product);	
		try {
			transactionRepository.save(transaction);
		}catch(Exception ex) {
			 log.info("Inside PaymentServiceImpl !! - Failing in transaction " +paymentRequest.toString()+ " time :: " +LocalDateTime.now());
			ex.getStackTrace();
		}
		
			
		paymentResponse.setTransactionRef(transaction.getTransactionRef());
		paymentResponse.setStatus(transaction.getStatus());
		paymentResponse.setMessage("SUCCESS");
		return paymentResponse;


	}
		
	private Transaction createTransactionEntity(Customer customer, Product product) {
		Transaction transaction = new Transaction ();
		transaction.setAmount(customer.getAmount());
		transaction.setCreatedAt(LocalDateTime.now());
		transaction.setUpdatedAt(LocalDateTime.now());
		transaction.setCustomerId(customer.getCustomerId());
		transaction.setCurrency(product.getCurrency());
		transaction.setMerchantId(String.valueOf(MerchantCodes.MARCH001));
		transaction.setTransactionRef(UUID.randomUUID().toString());
		transaction.setWalletFee(customer.getWalletAmount());
		transaction.setStatus(TransactionStatus.INITIATED);
		return transaction;
	}

	private Product checkValidateProduct(String productId) throws ProductNotFoundException {
		 Optional<Product> product = productRepository.findByProductId(productId);
		 if(product.isEmpty()) {
				throw new ProductNotFoundException("Product Not Found !!");
			} else if(!product.get().getStatus().equals(CUSTOMER_STATUS)) {
				throw new ProductNotFoundException("Product is Not Active !!");
			}else {
			return product.get();
		}
	}

	private Customer checkValidateCustomer(String customerId) throws CustomerNotFoundException {
		Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
		if(customer.isEmpty()) {
			throw new CustomerNotFoundException("Customer Not Found !!");
		} else if(!customer.get().getStatus().equals(CUSTOMER_STATUS)) {
			throw new CustomerNotFoundException("Customer is not Active !!");
		}else {
		return customer.get();
	}
	}

}
