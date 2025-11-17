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

    // Đường dẫn lưu ảnh – đã chuẩn 100%
    @Value("${app.upload.dir:${user.dir}/src/main/resources/static/images/uploads/}")
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
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    public Book addBook(Book book, MultipartFile file) throws IOException {
        createUploadDirectory();

        if (file != null && !file.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            Path copyLocation = Paths.get(uploadDir).resolve(fileName);

            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            book.setImage("/images/uploads/" + fileName); // ĐÚNG 100%
        } else {
            book.setImage("/images/no-image.jpg");
        }

        return saveSafely(book, "Lỗi lưu sách");
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails, MultipartFile file) throws IOException {
        createUploadDirectory();

        Book existingBook = getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách ID: " + id));

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPrice(bookDetails.getPrice());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setCategory(bookDetails.getCategory());
        existingBook.setFeatured(bookDetails.isFeatured());
        existingBook.setBestseller(bookDetails.isBestseller());

        if (file != null && !file.isEmpty()) {
            // FIX 1: XÓA ẢNH CŨ CHUẨN 100%
            if (existingBook.getImage() != null &&
                    existingBook.getImage().startsWith("/images/uploads/") &&
                    !existingBook.getImage().contains("no-image.jpg")) {

                String oldFileName = existingBook.getImage().substring("/images/uploads/".length());
                Path oldPath = Paths.get(uploadDir).resolve(oldFileName);
                Files.deleteIfExists(oldPath);
            }

            // Lưu ảnh mới
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            Path copyLocation = Paths.get(uploadDir).resolve(fileName);

            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            existingBook.setImage("/images/uploads/" + fileName); // ĐÚNG 100%
        }
        // Không đổi ảnh → giữ nguyên

        return saveSafely(existingBook, "Lỗi cập nhật sách");
    }

    @Override
    @Transactional
    public void deleteBook(Long id) throws IOException {
        Book book = getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại ID: " + id));

        // FIX 2: XÓA ẢNH KHI XÓA SÁCH – CHUẨN 100%
        if (book.getImage() != null &&
                book.getImage().startsWith("/images/uploads/") &&
                !book.getImage().contains("no-image.jpg")) {

            String fileName = book.getImage().substring("/images/uploads/".length());
            Path imagePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(imagePath);
        }

        bookRepository.deleteById(id);
    }

    // === CÁC METHOD KHÁC GIỮ NGUYÊN 100% ===
    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAllBooks();
        return safeList(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword));
    }

    @Override
    public List<Book> getBooksByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) return getAllBooks();
        return safeList(bookRepository.findByCategoryId(categoryId));
    }

    @Override
    public List<Book> getNewestBooks() {
        return bookRepository.findTop8ByOrderByCreatedAtDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> getTopPopularBooks() {
        return bookRepository.findTop8ByOrderByPopularCountDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementPopularCount(Long bookId) {
        if (bookId == null) return;
        Book book = getBookById(bookId).orElseThrow();
        book.setPopularCount(book.getPopularCount() + 1);
        bookRepository.save(book);
    }

    @Override
    public Long countBooksByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) return 0L;
        return bookRepository.countByCategoryId(categoryId);
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