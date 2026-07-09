package com.notification.userservice.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CsvValidationException extends RuntimeException {

    private final Map<Long, List<String>> errors;

    public CsvValidationException(Map<Long, List<String>> errors, String message) {
        super(message);
        this.errors = errors;
    }
}
