package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 1. Phương thức này cần thiết cho logic update/save kiểm tra trùng lặp
    boolean existsByNameIgnoreCase(String name);

    // Phương thức tùy chọn: Tìm danh mục theo tên (không phân biệt hoa thường)
    Optional<Category> findByNameIgnoreCase(String name);
}