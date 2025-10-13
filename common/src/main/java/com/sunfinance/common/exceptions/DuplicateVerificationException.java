package com.sunfinance.common.exceptions;

public class DuplicateVerificationException extends VerificationException {
     public DuplicateVerificationException() {
        super("Duplicated verification.");
    }
}
