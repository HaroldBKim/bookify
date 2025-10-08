package project.bookify.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.bookify.entity.Reservation;
import project.bookify.entity.ResourceType;
import project.bookify.entity.User;
import project.bookify.redis.RedisSlotManager;
import project.bookify.repository.ReservationRepository;
import project.bookify.repository.RoomRepository;
import project.bookify.repository.SeatRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationConsumer {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final RedisSlotManager redisSlotManager;

    @KafkaListener(topics = "reservation.requests", groupId = "reservation-service")
    @Transactional
    public void handleReservation(ReservationRequestEvent event) {
        List<String> keys = buildSlotKeys(event.resourceType(), event.resourceId(),
                event.startTime(), event.endTime());

        try {
            // 1) 자원 존재 검증
            switch (event.resourceType()) {
                case ROOM -> roomRepository.findById(event.resourceId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 회의실"));
                case SEAT -> seatRepository.findById(event.resourceId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 좌석"));
            }

            // 2) 겹치는 예약 확인
            boolean exists = reservationRepository.existsByResourceTypeAndResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                    event.resourceType(), event.resourceId(), "CONFIRMED",
                    event.endTime(), event.startTime()
            );
            if (exists) {
                redisSlotManager.releaseHold(keys); // 중복이면 해제
                return;
            }

            // 3) DB 저장
            Reservation r = new Reservation();
            User u = new User();
            u.setId(event.userId());
            r.setUser(u);
            r.setResourceType(event.resourceType());
            r.setResourceId(event.resourceId());
            r.setStartTime(event.startTime());
            r.setEndTime(event.endTime());
            r.setStatus("CONFIRMED");
            reservationRepository.save(r);

            // 4) Redis TTL 확정 (예약 길이)
            int ttl = (int) Duration.between(LocalDateTime.now(), event.endTime()).getSeconds();
            redisSlotManager.confirmHold(keys, Math.max(ttl, 1));

        } catch (Exception e) {
            redisSlotManager.releaseHold(keys); // DB 실패 시 해제
            throw e;
        }
    }

    private List<String> buildSlotKeys(ResourceType type, Long id,
                                       LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime cur = start;
        while (cur.isBefore(end)) {
            slots.add(cur);
            cur = cur.plusMinutes(30);
        }
        return slots.stream()
                .map(st -> "bkf:slot:" + type + ":" + id + ":" + st.format(f))
                .toList();
    }
}
