package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Spring Data JPA tự hiểu và sinh query đúng
    boolean existsByNameIgnoreCase(String name);

    // Tùy chọn (rất khuyến khích giữ lại)
    Optional<Category> findByNameIgnoreCase(String name);
}