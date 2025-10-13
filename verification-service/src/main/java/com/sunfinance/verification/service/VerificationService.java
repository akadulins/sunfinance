package com.sunfinance.verification.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.sunfinance.common.dto.ConfirmVerificationRequest;
import com.sunfinance.common.dto.CreateVerificationRequest;
import com.sunfinance.common.events.*;
import com.sunfinance.common.exceptions.DuplicateVerificationException;
import com.sunfinance.common.exceptions.InvalidCodeException;
import com.sunfinance.common.exceptions.VerificationExpiredException;
import com.sunfinance.common.exceptions.VerificationNotFoundException;
import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.SubjectType;
import com.sunfinance.common.model.Verification;
import com.sunfinance.verification.config.VerificationConfig;
import com.sunfinance.verification.domain.events.DomainEventPublisher;
import com.sunfinance.verification.repository.VerificationRepository;

@Service
public class VerificationService {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VerificationService.class);

    private final VerificationRepository verificationRepository;
    private final VerificationConfig verificationConfig;
    private final DomainEventPublisher eventPublisher;

    public VerificationService(VerificationRepository verificationRepository,
            VerificationConfig verificationConfig,
            DomainEventPublisher eventPublisher) {
    			this.verificationRepository = verificationRepository;
    			this.verificationConfig = verificationConfig;
    			this.eventPublisher = eventPublisher;
    }	

    public UUID createVerification(CreateVerificationRequest request, String userInfo) {
        Subject subject = new Subject(request.subject().getIdentity(), request.subject().getType());

        verificationRepository.findBySubjectIdentityAndSubjectTypeAndConfirmedIsFalse(
                subject.getIdentity(), subject.getType())
                .ifPresent(v -> { throw new DuplicateVerificationException(); });

        String code = generateCode(verificationConfig.getCodeLength());
        log.info("Verification TTL = " + verificationConfig.getValidityPeriod());
        
        Verification verification = new Verification(subject, userInfo, code, verificationConfig.getValidityPeriod());
        verificationRepository.save(verification);

        eventPublisher.publish(new VerificationCreated(verification.getId(), code, subject));
        return verification.getId();
    }

    public void confirmVerification(UUID id, ConfirmVerificationRequest request, String userInfo) throws VerificationExpiredException {
        Verification verification = verificationRepository.findById(id).orElseThrow(VerificationNotFoundException::new);
        try {
            verification.confirm(request.code(), userInfo);
            verificationRepository.save(verification);
            if (verification.isConfirmed()) {
                eventPublisher.publish(new VerificationConfirmed(verification.getId(), verification.getCode(), verification.getSubject() ));
            }
        } catch (InvalidCodeException e) {
            eventPublisher.publish(new VerificationConfirmationFailed(verification.getId(), verification.getCode(), verification.getSubject()));
            throw e;
        }
    }

    private String generateCode(int length) {
        int max = (int)Math.pow(10, length) - 1;
        int code = ThreadLocalRandom.current().nextInt(0, max + 1);
        return String.format("%0" + length + "d", code);
    }
} 