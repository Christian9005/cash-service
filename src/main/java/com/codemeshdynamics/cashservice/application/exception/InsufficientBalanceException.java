package com.codemeshdynamics.cashservice.application.exception;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException() {
        super("Balance not available");
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
