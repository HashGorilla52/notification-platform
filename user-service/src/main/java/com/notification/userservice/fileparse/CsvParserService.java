package com.notification.userservice.fileparse;

import com.notification.userservice.exception.csv.CsvHeadersException;
import lombok.Getter;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CsvParserService {
    @Getter
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    public static EmailValidator getEmailValidator() {
        return EMAIL_VALIDATOR;
    }

    /**
     * Validate CSV headers. If {@code actualHeaders} contain all {@code requiredHeaders}, the method
     * won't do anything. Otherwise, it will drop {@link CsvHeadersException}.
     * @param requiredHeaders
     * @param actualHeaders
     */
    public void validateHeaders(Set<String> requiredHeaders, Set<String> actualHeaders) {
        Set<String> missingHeaders = requiredHeaders.stream().filter(
                header -> !actualHeaders.contains(header)).collect(Collectors.toSet());
        if (!missingHeaders.isEmpty()) {
            throw new CsvHeadersException("Required header(s) are missing, check missingHeaders" +
                    " and actualHeaders fields",  missingHeaders,
                    actualHeaders);
        }
    }
}
