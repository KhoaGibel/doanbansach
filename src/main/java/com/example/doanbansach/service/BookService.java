package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Book;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getAllBooks();

    // Cập nhật phương thức thêm sách
    void addBook(Book book, MultipartFile imageFile) throws IOException;

    Optional<Book> getBookById(Long id);

    // Cập nhật phương thức sửa sách
    void updateBook(Long id, Book bookDetails, MultipartFile imageFile) throws IOException;

    void deleteBook(Long id) throws IOException; // Phương thức xóa có thể cần IOException

    List<Book> getBooksByCategory(Long categoryId);
    Long countBooksByCategory(Long categoryId);
    List<Book> searchBooks(String keyword);
    List<Book> getNewestBooks();
    List<Book> getTopPopularBooks();
    void incrementPopularCount(Long bookId);
}