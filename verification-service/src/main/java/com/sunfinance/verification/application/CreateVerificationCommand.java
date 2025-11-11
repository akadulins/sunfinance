package com.sunfinance.verification.application;

public record CreateVerificationCommand(
        String identity,
        String type,
        String userInfo
) {}