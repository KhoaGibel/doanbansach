package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Phương thức quan trọng để Spring Security tìm user
    Optional<User> findByUsername(String username);
}