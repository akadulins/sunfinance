package com.sunfinance.verification.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunfinance.common.events.*;
import com.sunfinance.common.exceptions.*;
import com.sunfinance.common.model.*;
import com.sunfinance.verification.application.CreateVerificationCommand;
import com.sunfinance.verification.application.ConfirmVerificationCommand;
import com.sunfinance.verification.config.VerificationConfig;
import com.sunfinance.verification.domain.events.DomainEventPublisher;
import com.sunfinance.verification.domain.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
public class VerificationService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VerificationService.class);

	private final VerificationRepository verificationRepository;
	private final VerificationConfig config;
	private final OutboxEventRepository outboxRepository;
	private final DomainEventPublisher eventPublisher;

	public VerificationService(VerificationRepository verificationRepository, VerificationConfig config,
			OutboxEventRepository outboxRepository, DomainEventPublisher eventPublisher) {
		this.verificationRepository = verificationRepository;
		this.config = config;
		this.outboxRepository = outboxRepository;
		this.eventPublisher = eventPublisher;
	}

	public UUID createVerification(CreateVerificationCommand command) {
		log.info("Creating verification for identity: {}", command.identity());

		Subject subject = new Subject(command.identity(), SubjectType.fromValue(command.type()));

		verificationRepository.findPendingBySubject(subject).ifPresent(v -> {
			throw new DuplicateVerificationException();
		});

		String code = generateCode(config.getCodeLength());

		Verification verification = new Verification(subject, command.userInfo(), code, config.getValidityPeriod());

		verificationRepository.save(verification);

		outboxRepository.save(new OutboxEvent("verification.created", verification.toDomainEventJson()));

		log.info("Verification created: {}", verification.getId());
		return verification.getId();
	}

	public void confirmVerification(ConfirmVerificationCommand command) throws VerificationExpiredException {
		log.info("Confirming verification: {}", command.verificationId());

		Verification verification = verificationRepository.findById(command.verificationId())
				.orElseThrow(VerificationNotFoundException::new);

		try {

			verification.confirm(command.code(), command.userInfo());

			verificationRepository.save(verification);

			if (verification.isConfirmed()) {
				eventPublisher.publish(new VerificationConfirmed(verification.getId(), verification.getCode(),
						verification.getSubject()));
				log.info("Verification confirmed: {}", verification.getId());
			}

		} catch (InvalidCodeException e) {

			eventPublisher.publish(new VerificationConfirmationFailed(verification.getId(), verification.getCode(),
					verification.getSubject()));
			log.warn("Invalid code for verification: {}", verification.getId());
			throw e;
		}
	}

	private String generateCode(int length) {
		int max = (int) Math.pow(10, length) - 1;
		int code = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, max + 1);
		return String.format("%0" + length + "d", code);
	}
}