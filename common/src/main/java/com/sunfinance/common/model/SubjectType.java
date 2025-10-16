package com.sunfinance.common.model;

public enum SubjectType {
    EMAIL_CONFIRMATION("email_confirmation"),
    MOBILE_CONFIRMATION("mobile_confirmation");

    private final String type;

    SubjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SubjectType fromString(String type) {
        for (SubjectType st : values()) {
            if (st.type.equals(type)) {
                return st;
            }
        }
        throw new IllegalArgumentException("Unknown subject type: " + type);
    }
}