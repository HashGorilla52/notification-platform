package com.notification.userservice.fileparse;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ContactCsvHeader {
    NAME("name", true),
    EMAIL("email", true),
    PHONE("phone", false),
    TELEGRAM("telegram", false);

    private final String value;
    @Getter
    private final boolean required;
    private static final Set<String> requiredHeaders = Set.of(Arrays.stream(ContactCsvHeader.values())
            .filter(ContactCsvHeader::isRequired)
                .map(ContactCsvHeader::getValue).toArray(String[]::new));

    ContactCsvHeader(String value,  boolean required) {
        this.value = value;
        this.required = required;
    }

    @JsonValue
    public String getValue() {return this.value;}

    public static Set<String> getRequiredHeaders() {return requiredHeaders;}
}
