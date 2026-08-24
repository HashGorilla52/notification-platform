package com.notification.userservice.exception.csv;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CsvValidationException extends RuntimeException {

    public CsvValidationException(String message) {
        super(message);
    }
}
