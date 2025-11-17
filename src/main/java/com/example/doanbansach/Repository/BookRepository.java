package com.example.doanbansach.Repository; // Hoặc .repository

import com.example.doanbansach.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Dùng cho searchBooks
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String titleKeyword, String authorKeyword);

    // Dùng cho getBooksByCategory
    List<Book> findByCategoryId(Long categoryId);

    // Dùng cho countBooksByCategory
    Long countByCategoryId(Long categoryId);

    // SỬA LỖI: Bổ sung phương thức bị thiếu cho Sách Mới Nhất
    List<Book> findTop8ByOrderByCreatedAtDesc();

    // SỬA LỖI: Bổ sung phương thức bị thiếu cho Sách Bán Chạy
    List<Book> findTop8ByOrderByPopularCountDesc();
}