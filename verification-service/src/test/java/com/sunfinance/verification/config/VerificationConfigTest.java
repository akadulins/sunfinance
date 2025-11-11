package com.sunfinance.verification.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class VerificationConfigTest {

    @Test
    @DisplayName("Should have default values")
    void shouldHaveDefaultValues() {
        VerificationConfig config = new VerificationConfig();
        
        assertEquals(6, config.getCodeLength());
        assertEquals(Duration.ofMinutes(5), config.getValidityPeriod());
    }

    @Test
    @DisplayName("Should set custom values")
    void shouldSetCustomValues() {
        VerificationConfig config = new VerificationConfig();
        config.setCodeLength(8);
        config.setValidityPeriod(Duration.ofMinutes(10));
        
        assertEquals(8, config.getCodeLength());
        assertEquals(Duration.ofMinutes(10), config.getValidityPeriod());
    }
}