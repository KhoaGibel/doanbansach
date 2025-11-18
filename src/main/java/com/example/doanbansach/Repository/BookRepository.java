package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Tìm kiếm theo tiêu đề hoặc tác giả
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    // Lấy sách theo danh mục
    List<Book> findByCategoryId(Long categoryId);

    // Đếm sách theo danh mục
    Long countByCategoryId(Long categoryId);

    // Sách mới nhất (Top 8)
    List<Book> findTop8ByOrderByCreatedAtDesc();

    // Sách bán chạy (Top 8 theo lượt xem/mua)
    List<Book> findTop8ByOrderByPopularCountDesc();
}