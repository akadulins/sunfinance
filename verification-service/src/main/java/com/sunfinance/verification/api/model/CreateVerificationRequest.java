package com.sunfinance.verification.api.model;

import com.sunfinance.common.model.Subject;

public record CreateVerificationRequest(
        Subject subject
) {}

