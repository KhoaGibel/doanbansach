package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Tìm danh mục theo tên (không phân biệt hoa thường)
     * @param name Tên danh mục cần tìm
     * @return Optional chứa Category nếu tồn tại
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Kiểm tra xem tên danh mục đã tồn tại chưa (không phân biệt hoa thường)
     * @param name Tên danh mục cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByNameIgnoreCase(String name);
}