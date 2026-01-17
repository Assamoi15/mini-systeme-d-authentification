package com.example.mini_systeme_d.authentification.service;


import java.util.List;
import java.util.Map;

import com.example.mini_systeme_d.authentification.entity.User;
import com.example.mini_systeme_d.authentification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User registerAdmin(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        return user;
    }

    // Liste tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        userRepository.deleteById(id);
    }

    // Statistiques
    public Map<String, Object> getStats() {
        List<User> allUsers = userRepository.findAll();

        long totalUsers = allUsers.size();
        long admins = allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long users = allUsers.stream().filter(u -> "USER".equals(u.getRole())).count();

        return Map.of(
                "total", totalUsers,
                "admins", admins,
                "users", users
        );
    }

    // Trouver un utilisateur par username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    // Modifier le profil
    public User updateProfile(String username, String newUsername) {
        User user = findByUsername(username);
        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    // Changer le mot de passe
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}