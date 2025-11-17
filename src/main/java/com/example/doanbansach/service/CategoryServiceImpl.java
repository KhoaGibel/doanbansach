package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Category;
import com.example.doanbansach.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Category saveCategory(Category category) {
        String name = category.getName().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        // Kiểm tra trùng lặp
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            // Ném ra RuntimeException để Controller bắt và hiển thị lỗi
            throw new RuntimeException("Danh mục '" + name + "' đã tồn tại!");
        }
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại: " + id));

        // Nếu bạn đã có ràng buộc khóa ngoại (Foreign Key), dòng này sẽ là đủ
        categoryRepository.delete(category);
    }
}