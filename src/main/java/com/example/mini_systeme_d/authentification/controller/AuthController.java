package com.example.mini_systeme_d.authentification.controller;


import com.example.mini_systeme_d.authentification.controller.dto.LoginRequest;
import com.example.mini_systeme_d.authentification.controller.dto.RegisterRequest;
import com.example.mini_systeme_d.authentification.entity.User;
import com.example.mini_systeme_d.authentification.service.JwtService;
import com.example.mini_systeme_d.authentification.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(
                java.util.Map.of("message", "Utilisateur créé avec succès")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());

        // Générer le token avec le rôle
        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Connexion réussie",
                        "token", token,
                        "role", user.getRole()
                )
        );
    } // ← ACCOLADE MANQUANTE ICI !

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        userService.registerAdmin(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(
                java.util.Map.of("message", "Admin créé avec succès")
        );
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello backend";
    }
}



