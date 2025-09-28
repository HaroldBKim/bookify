package project.bookify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.bookify.entity.ResourceType;
import project.bookify.entity.User;
import project.bookify.repository.UserRepository;
import project.bookify.service.ReservationAsyncService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/async-reservations")
@RequiredArgsConstructor
public class ReservationAsyncController {

    private final ReservationAsyncService reservationAsyncService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> requestReservation(@RequestBody Map<String, String> req) {
        User user = userRepository.findById(Long.parseLong(req.get("userId")))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        reservationAsyncService.requestReservation(
                user,
                ResourceType.valueOf(req.get("resourceType")),
                Long.parseLong(req.get("resourceId")),
                LocalDateTime.parse(req.get("startTime")),
                LocalDateTime.parse(req.get("endTime"))
        );

        return ResponseEntity.ok("예약 요청이 접수되었습니다.");
    }

}
