package com.banking.exceptions;

public class ExistingAccountException extends RuntimeException {
	
	public ExistingAccountException(String message) {
		super(message);
	}
}
