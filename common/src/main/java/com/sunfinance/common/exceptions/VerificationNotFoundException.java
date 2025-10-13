package com.sunfinance.common.exceptions;

//404 Not Found
public class VerificationNotFoundException extends VerificationException {
 public VerificationNotFoundException() {
     super("Verification not found.");
 }
}