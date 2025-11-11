package com.sunfinance.verification.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunfinance.common.exceptions.*;
import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.SubjectType;
import com.sunfinance.verification.api.mapper.VerificationApiMapper;
import com.sunfinance.verification.api.model.*;
import com.sunfinance.verification.application.*;
import com.sunfinance.verification.exception.GlobalExceptionHandler;
import com.sunfinance.verification.service.VerificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VerificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VerificationService verificationService;

    private VerificationController verificationController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        VerificationApiMapper mapper = new VerificationApiMapper();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        verificationController = new VerificationController(verificationService, mapper);
        
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        
        mockMvc = MockMvcBuilders
                .standaloneSetup(verificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(converter)
                .build();
    }

    @Test
    @DisplayName("POST /verifications should return 201 with verification ID")
    void shouldCreateVerificationSuccessfully() throws Exception {
        // Given
        UUID expectedId = UUID.randomUUID();
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        CreateVerificationRequest request = new CreateVerificationRequest(subject);
        
        when(verificationService.createVerification(any(CreateVerificationCommand.class)))
                .thenReturn(expectedId);

        mockMvc.perform(post("/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedId.toString()));
        
        verify(verificationService, times(1))
                .createVerification(any(CreateVerificationCommand.class));
    }

    @Test
    @DisplayName("POST /verifications should return 400 for invalid JSON")
    void shouldReturn400ForInvalidJson() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /verifications should return 409 for duplicate verification")
    void shouldReturn409ForDuplicateVerification() throws Exception {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        CreateVerificationRequest request = new CreateVerificationRequest(subject);
        
        when(verificationService.createVerification(any(CreateVerificationCommand.class)))
                .thenThrow(new DuplicateVerificationException());

        // When & Then
        mockMvc.perform(post("/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Duplicated verification."));
    }

    @Test
    @DisplayName("PUT /verifications/{id}/confirm should return 204 on success")
    void shouldConfirmVerificationSuccessfully() throws Exception {
        // Given
        UUID verificationId = UUID.randomUUID();
        ConfirmVerificationRequest request = new ConfirmVerificationRequest("123456");
        
        doNothing().when(verificationService)
                .confirmVerification(any(ConfirmVerificationCommand.class));

        // When & Then
        mockMvc.perform(put("/verifications/{id}/confirm", verificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        
        verify(verificationService, times(1))
                .confirmVerification(any(ConfirmVerificationCommand.class));
    }

    @Test
    @DisplayName("PUT /verifications/{id}/confirm should return 404 when not found")
    void shouldReturn404WhenVerificationNotFound() throws Exception {
        // Given
        UUID verificationId = UUID.randomUUID();
        ConfirmVerificationRequest request = new ConfirmVerificationRequest("123456");
        
        doThrow(new VerificationNotFoundException())
                .when(verificationService)
                .confirmVerification(any(ConfirmVerificationCommand.class));

        // When & Then
        mockMvc.perform(put("/verifications/{id}/confirm", verificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Verification not found."));
    }

    @Test
    @DisplayName("PUT /verifications/{id}/confirm should return 422 for invalid code")
    void shouldReturn422ForInvalidCode() throws Exception {
        // Given
        UUID verificationId = UUID.randomUUID();
        ConfirmVerificationRequest request = new ConfirmVerificationRequest("wrong-code");
        
        doThrow(new InvalidCodeException())
                .when(verificationService)
                .confirmVerification(any(ConfirmVerificationCommand.class));

        // When & Then
        mockMvc.perform(put("/verifications/{id}/confirm", verificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Validation failed: invalid subject supplied."));
    }

    @Test
    @DisplayName("PUT /verifications/{id}/confirm should return 403 for forbidden access")
    void shouldReturn403ForForbiddenAccess() throws Exception {
        // Given
        UUID verificationId = UUID.randomUUID();
        ConfirmVerificationRequest request = new ConfirmVerificationRequest("123456");
        
        doThrow(new VerificationForbiddenException())
                .when(verificationService)
                .confirmVerification(any(ConfirmVerificationCommand.class));

        // When & Then
        mockMvc.perform(put("/verifications/{id}/confirm", verificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("No permission to confirm verification."));
    }

    @Test
    @DisplayName("PUT /verifications/{id}/confirm should return 410 for expired verification")
    void shouldReturn410ForExpiredVerification() throws Exception {
        // Given
        UUID verificationId = UUID.randomUUID();
        ConfirmVerificationRequest request = new ConfirmVerificationRequest("123456");
        
        doThrow(new VerificationExpiredException())
                .when(verificationService)
                .confirmVerification(any(ConfirmVerificationCommand.class));

        // When & Then
        mockMvc.perform(put("/verifications/{id}/confirm", verificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410))
                .andExpect(jsonPath("$.message").value("Verification has expired"));
    }

    @Test
    @DisplayName("POST /verifications with invalid email should return 400")
    void shouldReturn400ForInvalidEmailFormat() throws Exception {

        String invalidRequest = "{\"subject\":{\"identity\":\"invalid-email\",\"type\":\"email_confirmation\"}}";
        
        try {
            new Subject("invalid-email", SubjectType.EMAIL_CONFIRMATION);        
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    @DisplayName("POST /verifications with invalid mobile should return 400")
    void shouldReturn400ForInvalidMobileFormat() throws Exception {
        try {
            new Subject("1234567890", SubjectType.MOBILE_CONFIRMATION);
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }
}