package project.bookify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
