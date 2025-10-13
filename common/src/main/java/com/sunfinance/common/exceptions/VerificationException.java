package com.sunfinance.common.exceptions;

public abstract class VerificationException extends RuntimeException {
    public VerificationException(String message) {
        super(message);
    }
}
