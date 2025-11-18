package com.example.doanbansach.Controller;

import com.example.doanbansach.Entity.Book;
import com.example.doanbansach.Entity.Category;
import com.example.doanbansach.service.BookService;
import com.example.doanbansach.service.CategoryService;
import com.example.doanbansach.service.CartService;
import com.example.doanbansach.service.OrderService;
import com.example.doanbansach.dto.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class BookController {

    @Autowired private BookService bookService;
    @Autowired private CategoryService categoryService;
    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Book> allBooks = bookService.getAllBooks();
        List<Category> categories = getSafeList(categoryService.getAllCategories());
        Map<Long, Long> categoryCounts = new HashMap<>();
        for (Category cat : categories) {
            Long count = bookService.countBooksByCategory(cat.getId());
            categoryCounts.put(cat.getId(), count != null ? count : 0L);
        }
        model.addAttribute("allBooks", allBooks);
        model.addAttribute("categories", categories);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("cartItems", getCartItemsWithQuantity());
        model.addAttribute("newestBooks", bookService.getNewestBooks());
        model.addAttribute("popularBooks", bookService.getTopPopularBooks());
        return "index";
    }

    @GetMapping("/admin/products")
    public String showAdminProductsPage(Model model) {
        model.addAttribute("allBooks", bookService.getAllBooks());
        model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", new Book());
        }
        return "admin-products";
    }

    @GetMapping("/books")
    public String viewBooksList(@RequestParam(required = false) String filter, Model model) {
        List<Book> books;
        if ("newest".equals(filter)) {
            books = bookService.getNewestBooks();
        } else if ("popular".equals(filter)) {
            books = bookService.getTopPopularBooks();
        } else {
            books = bookService.getAllBooks();
        }
        List<Category> categories = getSafeList(categoryService.getAllCategories());
        Map<Long, Long> categoryCounts = new HashMap<>();
        for (Category cat : categories) {
            Long count = bookService.countBooksByCategory(cat.getId());
            categoryCounts.put(cat.getId(), count != null ? count : 0L);
        }
        model.addAttribute("books", getSafeList(books));
        model.addAttribute("categories", categories);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("cartItems", getCartItemsWithQuantity());
        model.addAttribute("currentFilter", filter);
        return "books";
    }

    @PostMapping("/books")
    public String saveBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        if (book.getCategory() == null || book.getCategory().getId() == null) {
            bindingResult.rejectValue("category", "error.book", "Vui lòng chọn một danh mục.");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.book", bindingResult);
            redirectAttributes.addFlashAttribute("book", book);
            redirectAttributes.addFlashAttribute("error", "Lỗi: Vui lòng kiểm tra lại thông tin sách.");
            return "redirect:/admin/products";
        }
        try {
            Category category = categoryService.getCategoryById(book.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
            book.setCategory(category);
            bookService.addBook(book, imageFile);
            redirectAttributes.addFlashAttribute("success", "Thêm sách thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi lưu sách: " + e.getMessage());
            redirectAttributes.addFlashAttribute("book", book);
            e.printStackTrace();
            return "redirect:/admin/products";
        }
    }

    @GetMapping("/books/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Book book = bookService.getBookById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ID sách không tồn tại: " + id));
            model.addAttribute("book", book);
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            return "edit_book";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/books/{id}")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute("book") Book bookDetails,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bookDetails.getCategory() == null || bookDetails.getCategory().getId() == null) {
            bindingResult.rejectValue("category.id", "error.book", "Vui lòng chọn một danh mục.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            return "edit_book";
        }
        try {
            Category category = categoryService.getCategoryById(bookDetails.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
            bookDetails.setCategory(category);
            bookService.updateBook(id, bookDetails, imageFile);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sách thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi cập nhật sách: " + e.getMessage());
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            e.printStackTrace();
            return "edit_book";
        }
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes ra) throws IOException {
        try {
            bookService.deleteBook(id);
            ra.addFlashAttribute("success", "Xóa sách thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi xóa sách: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/books/detail/{id}")
    public String viewBookDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Book book = bookService.getBookById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + id));
            bookService.incrementPopularCount(id);
            model.addAttribute("book", book);
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            model.addAttribute("cartItems", getCartItemsWithQuantity());
            return "detail_book";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi tải chi tiết sách: " + e.getMessage());
            return "redirect:/books";
        }
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        List<Book> searchResults = bookService.searchBooks(keyword.trim());
        List<Category> categories = getSafeList(categoryService.getAllCategories());

        Map<Long, Long> categoryCounts = new HashMap<>();
        for (Category cat : categories) {
            Long count = bookService.countBooksByCategory(cat.getId());
            categoryCounts.put(cat.getId(), count != null ? count : 0L);
        }

        model.addAttribute("books", getSafeList(searchResults));
        model.addAttribute("keyword", keyword.trim());
        model.addAttribute("categories", categories);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("cartItems", getCartItemsWithQuantity());
        model.addAttribute("pageTitle", "Tìm kiếm: " + keyword.trim());

        if (searchResults.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy sách nào với từ khóa: '" + keyword.trim() + "'");
        }

        return "books";
    }

    // PHƯƠNG THỨC BẠN YÊU CẦU: Hiển thị sách theo Category ID
    @GetMapping("/category/{id}")
    public String viewBooksByCategory(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Category category = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

            List<Book> books = bookService.getBooksByCategory(id);
            List<Category> categories = getSafeList(categoryService.getAllCategories());

            // Tính số lượng sách cho từng Category (dùng cho sidebar)
            Map<Long, Long> categoryCounts = new HashMap<>();
            for (Category cat : categories) {
                Long count = bookService.countBooksByCategory(cat.getId());
                categoryCounts.put(cat.getId(), count != null ? count : 0L);
            }

            model.addAttribute("books", getSafeList(books));
            model.addAttribute("category", category);
            model.addAttribute("categories", categories);
            model.addAttribute("categoryCounts", categoryCounts);
            model.addAttribute("cartItems", getCartItemsWithDetails()); // SỬA: Dùng hàm getCartItemsWithDetails đã fix
            model.addAttribute("pageTitle", "Danh mục: " + category.getName());

            return "books"; // Trả về file books.html (Card View)
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi tải danh mục: " + e.getMessage());
            return "redirect:/books";
        }
    }

    @GetMapping("/aboutus")
    public String showAboutUsPage() {
        return "aboutus";
    }

    // --- CÁC HÀM HỖ TRỢ ---
    private <T> List<T> getSafeList(List<T> list) {
        return list != null ? list : List.of();
    }

    // HÀM NÀY PHẢI ĐƯỢC CHUYỂN HOẶC GỌI TỪ CartController
    private List<CartItem> getCartItemsWithQuantity() {
        List<CartItem> items = new ArrayList<>();
        Map<Long, Integer> cartMap = cartService.getCart();
        if (cartMap == null) return items;
        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            bookService.getBookById(entry.getKey()).ifPresent(book ->
                    items.add(new CartItem(book, entry.getValue()))
            );
        }
        return items;
    }

    // SỬA: Hàm hỗ trợ cho viewBooksByCategory (lấy chi tiết sách từ ID trong giỏ)
    private List<CartItem> getCartItemsWithDetails() {
        List<CartItem> items = new ArrayList<>();
        Map<Long, Integer> cartMap = cartService.getCart();
        if (cartMap == null) return items;

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();

            bookService.getBookById(bookId).ifPresent(book -> {
                items.add(new CartItem(book, quantity));
            });
        }
        return items;
    }

    private double calculateTotal() {
        return getCartItemsWithQuantity().stream()
                .mapToDouble(item -> item.book().getPrice() * item.quantity())
                .sum();
    }
}