package com.example.doanbansach.Entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users") // Đổi tên bảng thành "users"
public class User implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Các trường khác của bạn (ví dụ: email, tên...)
    private String email;
    private String fullName;

    // TRƯỜNG QUAN TRỌNG NHẤT: Lưu vai trò
    @Column(nullable = false)
    private String role; // Ví dụ: "ROLE_ADMIN" hoặc "ROLE_USER"

    // Getters và Setters cho các trường trên...
    // (id, username, password, email, fullName, role)

    // === Các phương thức bắt buộc của UserDetails ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về một danh sách các quyền (vai trò) của người dùng
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    // Các phương thức này để kiểm tra tài khoản (cứ để true là được)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // (Tự tạo các Getters và Setters còn thiếu)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}