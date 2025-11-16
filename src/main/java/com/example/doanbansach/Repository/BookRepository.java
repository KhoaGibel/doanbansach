package com.example.doanbansach.Repository;

import com.example.doanbansach.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {


    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String titleKeyword, String authorKeyword);

    List<Book> findByCategoryId(Long categoryId);

    Long countByCategoryId(Long categoryId);


    List<Book> findTop8ByOrderByCreatedAtDesc();


    List<Book> findTop8ByOrderByPopularCountDesc();
}