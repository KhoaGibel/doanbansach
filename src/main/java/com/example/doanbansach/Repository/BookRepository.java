package com.example.doanbansach.Repository; // Hoặc "repository" (viết thường)

import com.example.doanbansach.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String keyword);

    List<Book> findByCategoryId(Long categoryId);

    Long countByCategoryId(Long categoryId);

    List<Book> findTop5ByOrderByCreatedAtDesc();

    List<Book> findTop5ByOrderByPopularCountDesc();
}