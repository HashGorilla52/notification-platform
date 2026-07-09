package com.notification.userservice.dto.contact;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UploadCsvResult(UUID taskId, ProcessingStatus status, long created, long errors, Map<Long, List<String>> errorDetails) {}
