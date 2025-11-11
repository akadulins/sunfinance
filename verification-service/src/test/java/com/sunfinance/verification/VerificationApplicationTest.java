package com.sunfinance.verification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class VerificationApplicationTest {

    @Test
    @DisplayName("Should load Spring context")
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }
}