package fr.iamdamba.javahelpdesklite.controllers;

import fr.iamdamba.javahelpdesklite.models.dtos.auth.LoginDto;
import fr.iamdamba.javahelpdesklite.models.dtos.auth.RegisterDto;
import fr.iamdamba.javahelpdesklite.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerRequest) {
        return authService.register(registerRequest);
    }
}
