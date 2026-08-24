package com.notification.userservice.exception.csv;

import lombok.Getter;

import java.util.Collection;

public class CsvHeadersException extends RuntimeException {
    @Getter
    private final Collection<String> missingHeaders;
    @Getter
    private final Collection<String> actualHeaders;

    public CsvHeadersException(String message,  Collection<String> missingHeaders, Collection<String> actualHeaders) {
        super(message);
        this.missingHeaders = missingHeaders;
        this.actualHeaders = actualHeaders;
    }
}
