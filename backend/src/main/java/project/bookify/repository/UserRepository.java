package project.bookify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.bookify.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);
}
