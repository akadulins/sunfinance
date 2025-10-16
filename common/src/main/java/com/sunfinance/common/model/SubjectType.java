package com.sunfinance.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubjectType {
    EMAIL_CONFIRMATION("email_confirmation"),
    MOBILE_CONFIRMATION("mobile_confirmation");

    private final String type;

    SubjectType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static SubjectType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("SubjectType cannot be null");
        }

        String normalized = value.toLowerCase();

        for (SubjectType st : values()) {
            String enumNormalized = st.type.toLowerCase();
            if (enumNormalized.equals(normalized)) {
                return st;
            }
        }

        throw new IllegalArgumentException("Unknown subject type: " + value);
    }
}