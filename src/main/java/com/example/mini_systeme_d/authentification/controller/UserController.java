package com.example.mini_systeme_d.authentification.controller;

import com.example.mini_systeme_d.authentification.service.JwtService;
import com.example.mini_systeme_d.authentification.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // Vérifier le token et extraire le username
    private String getUsernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant");
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token invalide");
        }

        return jwtService.extractUsername(token);
    }

    // Modifier son profil (username)
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        try {
            String currentUsername = getUsernameFromToken(authHeader);
            String newUsername = request.get("newUsername");

            userService.updateProfile(currentUsername, newUsername);

            return ResponseEntity.ok(
                    Map.of("message", "Profil mis à jour avec succès")
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Changer son mot de passe
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        try {
            String username = getUsernameFromToken(authHeader);
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            userService.changePassword(username, oldPassword, newPassword);

            return ResponseEntity.ok(
                    Map.of("message", "Mot de passe modifié avec succès")
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
