package project.bookify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.bookify.entity.User;
import project.bookify.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody Map<String, String> req) {
        User user = userService.register(
                req.get("employeeId"),
                req.get("password"),
                req.get("name"),
                req.get("email")
        );
        return ResponseEntity.ok(user);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> req) {
        User user = userService.login(req.get("employeeId"), req.get("password"));
        return ResponseEntity.ok("로그인 성공: " + user.getName());
    }
}
