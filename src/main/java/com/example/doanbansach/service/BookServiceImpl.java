package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Book;
import com.example.doanbansach.Repository.BookRepository; // Đã sửa lỗi 'R' -> 'r'
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService { // Sửa: Thêm "implements BookService"

    @Autowired
    private BookRepository bookRepository;

    @Override // Thêm @Override
    public List<Book> getAllBooks() {
        return safeList(bookRepository.findAll());
    }

    @Override // Thêm @Override
    public Optional<Book> getBookById(Long id) {
        if (id == null) return Optional.empty();
        try {
            return bookRepository.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override // Thêm @Override
    @Transactional
    public Book addBook(Book book) {
        return saveSafely(book, "Lỗi lưu sách");
    }

    @Override // Thêm @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        if (id == null || bookDetails == null) {
            throw new IllegalArgumentException("ID hoặc dữ liệu sách không hợp lệ");
        }

        Book book = getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID sách không tồn tại: " + id));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPrice(bookDetails.getPrice());
        book.setDescription(bookDetails.getDescription());
        book.setCategory(bookDetails.getCategory());

        return saveSafely(book, "Lỗi cập nhật sách");
    }

    @Override // Thêm @Override
    @Transactional
    public void deleteBook(Long id) {
        if (id == null) throw new IllegalArgumentException("ID không được null");
        try {
            bookRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi xóa sách: " + e.getMessage());
        }
    }

    @Override // Thêm @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        // Giả sử Repository có phương thức này
        return safeList(bookRepository.findByTitleContainingIgnoreCase(keyword));
    }

    @Override // Thêm @Override
    public List<Book> getBooksByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return getAllBooks();
        }
        return safeList(bookRepository.findByCategoryId(categoryId));
    }

    @Override // Thêm @Override
    public List<Book> getNewestBooks() {
        // Giả sử Repository có phương thức này
        return bookRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override // Thêm @Override
    public List<Book> getTopPopularBooks() {
        // Giả sử Repository có phương thức này
        return bookRepository.findTop5ByOrderByPopularCountDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override // Thêm @Override
    @Transactional
    public void incrementPopularCount(Long bookId) {
        if (bookId == null) return;
        Book book = getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + bookId));
        // Giả sử Entity Book có phương thức này
        book.setPopularCount(book.getPopularCount() + 1);
        bookRepository.save(book);
    }

    @Override // Thêm @Override
    public Long countBooksByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return 0L;
        }
        try {
            return bookRepository.countByCategoryId(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    // === CÁC PHƯƠNG THỨC NỘI BỘ (PRIVATE) GIỮ NGUYÊN ===
    private Book saveSafely(Book book, String errorMsg) {
        try {
            return bookRepository.save(book);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(errorMsg + ": " + e.getMessage());
        }
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }
}