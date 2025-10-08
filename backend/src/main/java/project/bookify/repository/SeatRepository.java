package project.bookify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
