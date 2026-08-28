package com.notification.userservice.error;

import java.util.Map;
import static java.util.Map.entry;

public class DataIntegrityErrorMapper {
    private static final Map<String, String> SQL_STATE_CODES =  Map.ofEntries(
            entry("23505", "duplicate value: record already exists"),
            entry("23503", "related record not found"),
            entry("230502", "required filed is missing"),
            entry("23P01", "value violates a check constraint")
    );

    private static final String defaultMessage = "violation of an integrity constraint(s)";

    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Method for receiving message for appropriate {@code }
     * @param sqlStateCode SQL state code for error
     * @return message for {@code sqlStateCode}
     */
    public String getMessageBySqlStateCode(String sqlStateCode) {
        return SQL_STATE_CODES.get(sqlStateCode);
    }
}
