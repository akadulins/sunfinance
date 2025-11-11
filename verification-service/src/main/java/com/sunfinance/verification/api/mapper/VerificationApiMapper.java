package com.sunfinance.verification.api.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sunfinance.common.model.Verification;
import com.sunfinance.verification.api.model.ConfirmVerificationRequest;
import com.sunfinance.verification.api.model.CreateVerificationRequest;
import com.sunfinance.verification.api.model.VerificationResponse;
import com.sunfinance.verification.application.ConfirmVerificationCommand;
import com.sunfinance.verification.application.CreateVerificationCommand;

@Component
public class VerificationApiMapper {

	
	public CreateVerificationCommand toCommand(CreateVerificationRequest request, String userInfo) {
		return new CreateVerificationCommand(request.subject().getIdentity(), request.subject().getType().toString(),
				userInfo
		);
	}

	
	public ConfirmVerificationCommand toConfirmCommand(UUID verificationId, ConfirmVerificationRequest request,
			String userInfo) {
		return new ConfirmVerificationCommand(verificationId, request.code(), userInfo);
	}

	
	public VerificationResponse toResponse(Verification verification) {
		return new VerificationResponse(verification.getId(), verification.getSubject().getIdentity(),
				verification.getSubject().getType().getType(), verification.getCode(), verification.getExpiresAt(),
				verification.isConfirmed());
	}
}
