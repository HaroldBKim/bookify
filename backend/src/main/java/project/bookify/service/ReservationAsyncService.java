package project.bookify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.bookify.entity.ResourceType;
import project.bookify.entity.User;
import project.bookify.kafka.ReservationProducer;
import project.bookify.kafka.ReservationRequestEvent;
import project.bookify.redis.RedisSlotManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationAsyncService {

    private final ReservationProducer producer;
    private final RedisSlotManager redisSlotManager;

    private static final int HOLD_TTL_SEC = 10; // 임시 홀드 TTL (DB 확정까지 버티는 시간)

    public void requestReservation(User user, ResourceType type, Long resourceId,
                                   LocalDateTime start, LocalDateTime end) {

        List<String> keys = buildSlotKeys(type, resourceId, start, end);

        // 1) Redis 임시 선점
        boolean acquired = redisSlotManager.acquireHold(keys, HOLD_TTL_SEC);
        if (!acquired) {
            throw new IllegalStateException("이미 예약된 시간대입니다.");
        }

        // 2) Kafka 이벤트 발행
        producer.sendReservationRequest(
                new ReservationRequestEvent(user.getId(), type, resourceId, start, end)
        );
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
