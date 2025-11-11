package com.sunfinance.verification.application;

import java.util.UUID;

public record ConfirmVerificationCommand(
        UUID verificationId,
        String code,
        String userInfo
) {}