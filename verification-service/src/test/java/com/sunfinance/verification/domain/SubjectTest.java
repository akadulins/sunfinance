package com.sunfinance.verification.domain;

import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.SubjectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SubjectTest {

    @Test
    @DisplayName("Should create valid email subject")
    void shouldCreateValidEmailSubject() {
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        assertEquals("test@example.com", subject.getIdentity());
    }

    @Test
    @DisplayName("Should create valid mobile subject")
    void shouldCreateValidMobileSubject() {
        Subject subject = new Subject("+1234567890", SubjectType.MOBILE_CONFIRMATION);
        assertEquals("+1234567890", subject.getIdentity());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "no-at", "@test.com", "test@"})
    @DisplayName("Should reject invalid emails")
    void shouldRejectInvalidEmails(String invalid) {
        assertThrows(IllegalArgumentException.class, 
            () -> new Subject(invalid, SubjectType.EMAIL_CONFIRMATION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890", "+123", "abc"})
    @DisplayName("Should reject invalid mobiles")
    void shouldRejectInvalidMobiles(String invalid) {
        assertThrows(IllegalArgumentException.class, 
            () -> new Subject(invalid, SubjectType.MOBILE_CONFIRMATION));
    }
}