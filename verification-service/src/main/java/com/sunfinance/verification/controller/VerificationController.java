package com.sunfinance.verification.controller;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunfinance.common.dto.ConfirmVerificationRequest;
import com.sunfinance.common.dto.CreateVerificationRequest;
import com.sunfinance.common.dto.CreateVerificationResponse;
import com.sunfinance.common.exceptions.VerificationExpiredException;
import com.sunfinance.verification.service.VerificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/verifications")
public class VerificationController {
    private final VerificationService service;

    public VerificationController(VerificationService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CreateVerificationResponse> create(
            @RequestBody CreateVerificationRequest req,
            HttpServletRequest servletRequest) {
        String userInfo = extractUserInfo(servletRequest);
        UUID id = service.createVerification(req, userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateVerificationResponse(id));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(
    		@PathVariable("id") UUID id,
            @RequestBody ConfirmVerificationRequest request,
            HttpServletRequest servletRequest) throws VerificationExpiredException {
        String userInfo = extractUserInfo(servletRequest);
        service.confirmVerification(id, request, userInfo);
        return ResponseEntity.noContent().build();
    }

    private String extractUserInfo(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String agent = request.getHeader("User-Agent");
        return ip + "|" + agent;
    }
}