package project.bookify.kafka;

import project.bookify.entity.ResourceType;

import java.time.LocalDateTime;

public record ReservationRequestEvent (
    Long userId,
    ResourceType resourceType,
    Long resourceId,
    LocalDateTime startTime,
    LocalDateTime endTime
) {}

