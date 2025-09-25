package project.bookify.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);
}
