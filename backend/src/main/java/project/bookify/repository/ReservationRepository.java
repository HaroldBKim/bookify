package project.bookify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.entity.Reservation;
import project.bookify.entity.ResourceType;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);

    // 주어진 시간 구간과 겹치는 예약이 존재하는지 확인
    boolean existsByResourceTypeAndResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            ResourceType type,
            Long resourceId,
            String status,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );
}
