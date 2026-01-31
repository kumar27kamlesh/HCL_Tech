package com.hcl.ewallet.payment.exception;

@SuppressWarnings("serial")
public class CustomerNotFoundException extends Exception {
	
	public CustomerNotFoundException(String message){
		super(message);
	}
}
