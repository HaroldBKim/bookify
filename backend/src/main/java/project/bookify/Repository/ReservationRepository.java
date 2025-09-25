package project.bookify.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.Entity.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
}
