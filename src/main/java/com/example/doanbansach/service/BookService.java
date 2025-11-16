package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> getAllBooks();

    Optional<Book> getBookById(Long id);

    Book addBook(Book book);

    Book updateBook(Long id, Book bookDetails);

    void deleteBook(Long id);

    List<Book> searchBooks(String keyword);

    List<Book> getBooksByCategory(Long categoryId);

    List<Book> getNewestBooks();

    List<Book> getTopPopularBooks();

    void incrementPopularCount(Long bookId);

    Long countBooksByCategory(Long categoryId);
}