package com.example.doanbansach.config;

import com.example.doanbansach.Entity.User;
import com.example.doanbansach.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. Tự động tạo bảng (do ddl-auto=update)
        // Bạn không cần làm gì cả, Hibernate sẽ tự tạo bảng

        // 2. Tự động tạo tài khoản ADMIN

        // Kiểm tra xem tài khoản "admin" đã tồn tại chưa
        if (userRepository.findByUsername("admin").isEmpty()) {

            System.out.println("Tạo tài khoản ADMIN mặc định...");

            User adminUser = new User();
            adminUser.setUsername("admin");

            // QUAN TRỌNG: Phải mã hóa mật khẩu
            adminUser.setPassword(passwordEncoder.encode("admin123")); // <-- Đổi mật khẩu ở đây

            adminUser.setRole("ROLE_ADMIN");

            // (Bạn có thể set thêm email, fullName... nếu muốn)
            // adminUser.setEmail("admin@example.com");
            // adminUser.setFullName("Quản Trị Viên");

            userRepository.save(adminUser);

            System.out.println("Tạo tài khoản ADMIN thành công!");
        }
    }
}