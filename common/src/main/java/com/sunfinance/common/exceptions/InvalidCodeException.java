package com.sunfinance.common.exceptions;

//422
public class InvalidCodeException extends VerificationException {
 public InvalidCodeException() {
     super("Validation failed: invalid subject supplied.");
 }
}