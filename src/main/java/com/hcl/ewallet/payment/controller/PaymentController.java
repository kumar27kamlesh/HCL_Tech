
package com.hcl.ewallet.payment.controller;

import com.hcl.ewallet.payment.exception.CustomerNotFoundException;
import com.hcl.ewallet.payment.exception.ProductNotFoundException;
import com.hcl.ewallet.payment.model.PaymentRequest;
import com.hcl.ewallet.payment.model.PaymentResponse;
import com.hcl.ewallet.payment.service.PaymentService;

import java.util.Date;

//import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ewallet")
@CrossOrigin(origins = "*")
public class PaymentController {
	
	private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
    private PaymentService paymentService;
	
	@GetMapping("/paymentstest")
    public String makePaymentTest(){
		return "Payment Success";
	}
    		

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> makePayment(
         @Valid   @RequestBody PaymentRequest paymentRequest) throws CustomerNotFoundException, ProductNotFoundException {
    	log.info("Inside PaymentController - Request start !! " +new Date()+ " time: "+new Date().getTime());
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        
        log.info("Inside PaymentController - Request end !! " +new Date()+ " time: "+new Date().getTime());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
