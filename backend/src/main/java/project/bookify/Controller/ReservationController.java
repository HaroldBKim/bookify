package project.bookify.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.bookify.Entity.Reservation;
import project.bookify.Entity.ResourceType;
import project.bookify.Entity.User;
import project.bookify.Repository.UserRepository;
import project.bookify.Service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Map<String, String> req) {
        User user = userRepository.findById(Long.parseLong(req.get("userId")))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Reservation reservation = reservationService.createReservation(
                user,
                ResourceType.valueOf(req.get("resourceType")),
                Long.parseLong(req.get("resourceId")),
                LocalDateTime.parse(req.get("startTime")),
                LocalDateTime.parse(req.get("endTime"))
        );

        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Reservation>> getMyReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getMyReservations(userId));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId,
                                                    @RequestParam Long userId) {
        reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }

}
