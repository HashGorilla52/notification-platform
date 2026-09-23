package com.notification.userservice.dto.contact;

import java.util.List;

public record ContactsCursorPage (List<ContactResponse> contacts, int cursor) {}
