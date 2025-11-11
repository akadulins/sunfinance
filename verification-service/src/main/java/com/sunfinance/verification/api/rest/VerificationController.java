package com.sunfinance.verification.api.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sunfinance.common.dto.CreateVerificationResponse;
import com.sunfinance.common.exceptions.VerificationExpiredException;
import com.sunfinance.verification.api.mapper.VerificationApiMapper;
import com.sunfinance.verification.api.model.*;
import com.sunfinance.verification.application.CreateVerificationCommand;
import com.sunfinance.verification.application.ConfirmVerificationCommand;
import com.sunfinance.verification.service.VerificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/verifications")
public class VerificationController {
    
    private final VerificationService service;
    private final VerificationApiMapper mapper; 

    public VerificationController(VerificationService service, VerificationApiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CreateVerificationResponse> create(
            @RequestBody CreateVerificationRequest request,
            HttpServletRequest servletRequest) {
        
        String userInfo = extractUserInfo(servletRequest);
        
        CreateVerificationCommand command = mapper.toCommand(request, userInfo);
 
        UUID id = service.createVerification(command);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateVerificationResponse(id));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(
            @PathVariable("id") UUID id,
            @RequestBody ConfirmVerificationRequest request,
            HttpServletRequest servletRequest) throws VerificationExpiredException {
        
        String userInfo = extractUserInfo(servletRequest);
        
        ConfirmVerificationCommand command = mapper.toConfirmCommand(id, request, userInfo);

        service.confirmVerification(command);
        
        return ResponseEntity.noContent().build();
    }

    private String extractUserInfo(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String agent = request.getHeader("User-Agent");
        return ip + "|" + agent;
    }
}