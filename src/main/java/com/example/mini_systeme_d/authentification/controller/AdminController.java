package com.example.mini_systeme_d.authentification.controller;

import com.example.mini_systeme_d.authentification.entity.User;
import com.example.mini_systeme_d.authentification.service.JwtService;
import com.example.mini_systeme_d.authentification.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JwtService jwtService;

    // Vérifier si l'utilisateur est ADMIN
    private ResponseEntity<?> checkAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Token manquant")
            );
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Token invalide")
            );
        }

        String role = jwtService.extractRole(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(
                    Map.of("error", "Accès réservé aux administrateurs")
            );
        }

        return null; // Pas d'erreur
    }

    // 1. Liste de tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> error = checkAdmin(authHeader);
        if (error != null) return error;

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 2. Supprimer un utilisateur
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        ResponseEntity<?> error = checkAdmin(authHeader);
        if (error != null) return error;

        userService.deleteUser(id);
        return ResponseEntity.ok(
                Map.of("message", "Utilisateur supprimé avec succès")
        );
    }

    // 3. Statistiques
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> error = checkAdmin(authHeader);
        if (error != null) return error;

        Map<String, Object> stats = userService.getStats();
        return ResponseEntity.ok(stats);
    }
}
