package project.bookify.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationProducer {

    private final KafkaTemplate<String, ReservationRequestEvent> requestKafka;
    private final KafkaTemplate<String, CancelReservationEvent> cancelKafka;

    public void sendReservationRequest(ReservationRequestEvent event){
        // Kafka 파티션 키 설정 - 자원 단위 직렬 처리 보장
        String key = event.resourceType().name() + "-" + event.resourceId();
        requestKafka.send(Topics.RESERVATION_REQUESTS, key, event);
    }

    public void sendReservationCancel(CancelReservationEvent event){
        String key = event.resourceType().name() + "-" + event.resourceId();
        cancelKafka.send(Topics.RESERVATION_CANCELLATIONS, key, event);
    }

}
