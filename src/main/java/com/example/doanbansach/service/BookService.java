package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> getAllBooks();

    Optional<Book> getBookById(Long id);

    // SỬA: Phải trả về Book (không phải void) để khớp với BookServiceImpl
    Book addBook(Book book, MultipartFile file) throws IOException;

    // SỬA: Phải trả về Book (không phải void)
    Book updateBook(Long id, Book bookDetails, MultipartFile file) throws IOException;

    void deleteBook(Long id) throws IOException;

    List<Book> searchBooks(String keyword);

    List<Book> getBooksByCategory(Long categoryId);

    List<Book> getNewestBooks();

    List<Book> getTopPopularBooks();

    void incrementPopularCount(Long bookId);

    Long countBooksByCategory(Long categoryId);
}