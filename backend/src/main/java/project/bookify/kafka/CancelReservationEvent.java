package project.bookify.kafka;

import project.bookify.entity.ResourceType;
import java.time.LocalDateTime;

public record CancelReservationEvent(
        Long userId,
        ResourceType resourceType,
        Long reservationId,
        Long resourceId ) {
}
