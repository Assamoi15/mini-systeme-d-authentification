package com.example.mini_systeme_d.authentification.controller;

import com.example.mini_systeme_d.authentification.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/protected")
@RequiredArgsConstructor
public class ProtectedController {

    private final JwtService jwtService;

    // Route accessible à TOUS les utilisateurs connectés
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(
                    java.util.Map.of("error", "Token manquant")
            );
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body(
                    java.util.Map.of("error", "Token invalide")
            );
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Accès autorisé",
                        "username", username,
                        "role", role
                )
        );
    }

    // Route réservée aux ADMIN uniquement ← NOUVEAU
    @GetMapping("/admin-only")
    public ResponseEntity<?> adminOnly(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(
                    java.util.Map.of("error", "Token manquant")
            );
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body(
                    java.util.Map.of("error", "Token invalide")
            );
        }

        String role = jwtService.extractRole(token);

        // Vérifier si l'utilisateur est ADMIN
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body( // ← 403 = Forbidden
                    java.util.Map.of("error", "Accès refusé : réservé aux administrateurs")
            );
        }

        String username = jwtService.extractUsername(token);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Bienvenue dans l'espace admin",
                        "username", username,
                        "role", role,
                        "info", "Vous avez les privilèges administrateur !"
                )
        );
    }
}