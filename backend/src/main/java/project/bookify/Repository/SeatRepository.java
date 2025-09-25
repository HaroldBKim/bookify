package project.bookify.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.Entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
