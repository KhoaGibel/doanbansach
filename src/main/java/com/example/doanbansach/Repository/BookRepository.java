package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 1. Dùng cho searchBooks (Tìm kiếm theo Title HOẶC Author)
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String titleKeyword, String authorKeyword);

    // 2. Dùng cho getBooksByCategory
    List<Book> findByCategoryId(Long categoryId);

    // 3. Dùng cho countBooksByCategory
    Long countByCategoryId(Long categoryId);

    // 4. Sửa lỗi: Cho Sách Mới Nhất
    List<Book> findTop5ByOrderByCreatedAtDesc();

    // 5. Cho Sách Bán Chạy
    List<Book> findTop5ByOrderByPopularCountDesc();
}