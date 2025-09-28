package project.bookify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.bookify.entity.User;
import project.bookify.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // 회원가입
    public User register(String employeeId, String password, String name, String email) {
        if (userRepository.findByEmployeeId(employeeId).isPresent()) {
            throw new RuntimeException("이미 존재하는 사번입니다.");
        }

        User user = new User();
        user.setEmployeeId(employeeId);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    // 로그인
    public User login(String employeeId, String password) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("사번을 찾을 수 없습니다."));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

}
