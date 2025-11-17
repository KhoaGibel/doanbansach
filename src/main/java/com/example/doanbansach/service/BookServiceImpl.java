package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Book;
import com.example.doanbansach.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private void createUploadDirectory() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu ảnh: " + uploadDir, e);
        }
    }

    @Override
    public List<Book> getAllBooks() {
        return safeList(bookRepository.findAll());
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        if (id == null) return Optional.empty();
        try {
            return bookRepository.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Book addBook(Book book, MultipartFile file) throws IOException {
        createUploadDirectory();

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path copyLocation = Paths.get(uploadDir + fileName);

            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            book.setImage(fileName);
        } else if (book.getImage() == null || book.getImage().isEmpty()) {
            book.setImage("default-book.png");
        }
        return saveSafely(book, "Lỗi lưu sách");
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails, MultipartFile file) throws IOException {
        createUploadDirectory();

        if (id == null || bookDetails == null) {
            throw new IllegalArgumentException("ID hoặc dữ liệu sách không hợp lệ");
        }

        Book existingBook = getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID sách không tồn tại: " + id));

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPrice(bookDetails.getPrice());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setCategory(bookDetails.getCategory());

        if (!file.isEmpty()) {
            if (existingBook.getImage() != null && !existingBook.getImage().equals("default-book.png")) {
                Path oldImagePath = Paths.get(uploadDir + existingBook.getImage());
                Files.deleteIfExists(oldImagePath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path copyLocation = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            existingBook.setImage(fileName);
        } else if (bookDetails.getImage() != null) {
            existingBook.setImage(bookDetails.getImage());
        }

        return saveSafely(existingBook, "Lỗi cập nhật sách");
    }

    @Override
    @Transactional
    public void deleteBook(Long id) throws IOException {
        if (id == null) throw new IllegalArgumentException("ID không được null");

        Optional<Book> bookOptional = getBookById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (book.getImage() != null && !book.getImage().equals("default-book.png")) {
                Path imagePath = Paths.get(uploadDir + book.getImage());
                Files.deleteIfExists(imagePath);
            }
            bookRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Sách không tồn tại với ID: " + id);
        }
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return safeList(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword));
    }

    @Override
    public List<Book> getBooksByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return getAllBooks();
        }
        return safeList(bookRepository.findByCategoryId(categoryId));
    }

    @Override
    public List<Book> getNewestBooks() {
        return bookRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> getTopPopularBooks() {
        return bookRepository.findTop5ByOrderByPopularCountDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementPopularCount(Long bookId) {
        if (bookId == null) return;
        Book book = getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + bookId));
        book.setPopularCount(book.getPopularCount() + 1);
        bookRepository.save(book);
    }

    @Override
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