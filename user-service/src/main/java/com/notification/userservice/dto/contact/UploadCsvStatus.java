package com.notification.userservice.dto.contact;

import java.util.UUID;

public record UploadCsvStatus(UUID taskId, ProcessingStatus status, String message) {
}
