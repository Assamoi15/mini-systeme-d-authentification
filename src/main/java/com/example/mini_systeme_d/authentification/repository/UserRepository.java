package com.example.mini_systeme_d.authentification.repository;


import com.example.mini_systeme_d.authentification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
