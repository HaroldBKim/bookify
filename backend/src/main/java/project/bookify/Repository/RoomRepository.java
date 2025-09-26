package project.bookify.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.Entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
