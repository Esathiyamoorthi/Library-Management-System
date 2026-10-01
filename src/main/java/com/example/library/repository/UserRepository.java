package com.example.library.repository;

import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // "Is there any user with this email?"  Spring writes the SQL from the method name.
    boolean existsByEmail(String email);

    // Same, but ignoring one user (used when a user updates their own profile).
    boolean existsByEmailAndIdNot(String email, Long id);
}