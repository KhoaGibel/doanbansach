package com.example.doanbansach.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Thêm import này

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @Column(nullable = false)
    @NotNull(message = "Giá không được để trống") // Thêm @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0") // Sửa: inclusive = false
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "popular_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int popularCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Danh mục không được để trống") // SỬA LỖI VALIDATION
    private Category category;

    // === BỔ SUNG 2 TRƯỜNG MỚI ===
    @Column(name = "is_featured", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean featured = false;

    @Column(name = "is_bestseller", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean bestseller = false;
    // === KẾT THÚC BỔ SUNG ===


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Book() {}

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author.trim() : null;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPopularCount() {
        return popularCount;
    }

    public void setPopularCount(int popularCount) {
        this.popularCount = popularCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // === GETTERS/SETTERS CHO TRƯỜNG MỚI ===
    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isBestseller() {
        return bestseller;
    }

    public void setBestseller(boolean bestseller) {
        this.bestseller = bestseller;
    }
    // === KẾT THÚC GETTERS/SETTERS MỚI ===

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price + "VNĐ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}