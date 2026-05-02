package com.codemeshdynamics.cashservice.infrastructure.controller;

import com.codemeshdynamics.cashservice.application.dto.request.AuthRequest;
import com.codemeshdynamics.cashservice.application.dto.response.AuthResponse;
import com.codemeshdynamics.cashservice.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
