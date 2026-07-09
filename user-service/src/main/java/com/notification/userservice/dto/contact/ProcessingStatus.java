package com.notification.userservice.dto.contact;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessingStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;
    ProcessingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
