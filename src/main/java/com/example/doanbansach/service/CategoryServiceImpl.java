package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Category;
import com.example.doanbansach.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional // THÊM: Đánh dấu giao dịch
    public Category saveCategory(Category category) {
        String name = category.getName().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Danh mục '" + name + "' đã tồn tại!");
        }
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional // THÊM: Đánh dấu giao dịch
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại: " + id));

        String newName = categoryDetails.getName().trim();
        if (newName.isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }

        if (!category.getName().equalsIgnoreCase(newName) &&
                categoryRepository.existsByNameIgnoreCase(newName)) {
            throw new RuntimeException("Danh mục '" + newName + "' đã tồn tại!");
        }

        category.setName(newName);
        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional // THÊM: Đánh dấu giao dịch
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại: " + id));

        // CHÚ Ý: Đảm bảo bạn đang chạy trong transaction để access getBooks()
        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục '" + category.getName() +
                    "' vì đang có " + category.getBooks().size() + " sách!");
        }
        categoryRepository.delete(category);
    }
}