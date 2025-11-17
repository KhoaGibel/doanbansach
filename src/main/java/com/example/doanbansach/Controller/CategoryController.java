package com.example.doanbansach.Controller;

import com.example.doanbansach.Entity.Category;
import com.example.doanbansach.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. HIỂN THỊ DANH SÁCH (GET /categories)
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    // 2. HIỂN THỊ FORM (FIX LỖI 400: Đổi tên path để tránh xung đột với ID)
    @GetMapping("/category/create")
    public String showNewCategoryForm(Model model) {
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        return "new_category";
    }

    // 3. LƯU DỮ LIỆU (FIX LỖI 405: Đảm bảo POST khớp với GET)
    @PostMapping("/category/create")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", bindingResult);
            redirectAttributes.addFlashAttribute("category", category);
            return "redirect:/categories/category/create"; // Lỗi: Quay lại form
        }

        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công!");
            return "redirect:/categories"; // Thành công: Quay về danh sách
        } catch (Exception e) {
            // FIX LỖI 500: Bắt lỗi Service và quay lại form
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("category", category);
            return "redirect:/categories/category/create";
        }
    }

    // 4. HIỂN THỊ FORM SỬA
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
        model.addAttribute("category", category);
        return "edit_category";
    }

    // 5. CẬP NHẬT DỮ LIỆU
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Lỗi: Vui lòng kiểm tra lại thông tin danh mục.");
            model.addAttribute("category", category);
            return "edit_category";
        }
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("category", category);
            return "edit_category";
        }
    }

    // 6. XÓA
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
}