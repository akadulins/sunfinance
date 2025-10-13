package com.sunfinance.common.exceptions;

//403 Forbidden
public class VerificationForbiddenException extends VerificationException {
    public VerificationForbiddenException() {
        super("No permission to confirm verification.");
    }
}