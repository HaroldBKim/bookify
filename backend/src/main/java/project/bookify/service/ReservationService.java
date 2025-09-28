package project.bookify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.bookify.entity.Reservation;
import project.bookify.entity.ResourceType;
import project.bookify.entity.User;
import project.bookify.repository.ReservationRepository;
import project.bookify.repository.RoomRepository;
import project.bookify.repository.SeatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    // 예약 생성
    public Reservation createReservation(User user, ResourceType type, Long resourceId,
                                         LocalDateTime start, LocalDateTime end) {

        switch (type) {
            case ROOM -> roomRepository.findById(resourceId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 회의실입니다."));
            case SEAT -> seatRepository.findById(resourceId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 좌석입니다."));
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResourceType(type);
        reservation.setResourceId(resourceId);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setStatus("CONFIRMED");

        return reservationRepository.save(reservation);
    }

    // 예약 조회
    public List<Reservation> getMyReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // 예약 취소
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 예약만 취소할 수 있습니다.");
        }

        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);
    }
}
