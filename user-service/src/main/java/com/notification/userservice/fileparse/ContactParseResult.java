package com.notification.userservice.fileparse;

import com.notification.userservice.entity.Contact;

import java.util.List;
import java.util.Map;

public record ContactParseResult(List<Contact> contacts, Map<Long, List<String>> errors) {
}
