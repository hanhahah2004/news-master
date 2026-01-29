package com.news.controller;

import com.news.common.result.Result;
import com.news.dto.LoginDTO;
import com.news.dto.RegisterDTO;
import com.news.dto.UserDTO;
import com.news.security.UserPrincipal;
import com.news.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);

        return ResponseEntity.ok(Result.success(Map.of("token", token, "type", "Bearer")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Result.success(userService.getCurrentUser(userPrincipal.getUserId())));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(
            @Valid @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userDTO.setId(userPrincipal.getUserId());
        userService.updateUser(userDTO);
        return ResponseEntity.ok().build();
    }
}
