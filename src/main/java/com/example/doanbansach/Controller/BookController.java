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

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

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
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tải danh sách sách: " + e.getMessage());
            model.addAttribute("books", List.of());
            model.addAttribute("categories", List.of());
            model.addAttribute("categoryCounts", Map.of());
            e.printStackTrace();
            return "books";
        }
    }

    @PostMapping("/books")
    public String saveBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.book", bindingResult);
            redirectAttributes.addFlashAttribute("book", book);
            redirectAttributes.addFlashAttribute("error", "Lỗi: Vui lòng kiểm tra lại thông tin sách.");
            return "redirect:/admin/products";
        }

        try {
            // FIX LỖI VALIDATION: Thiết lập Category trước khi gọi Service
            if (categoryId != null && categoryId > 0) {
                Category category = categoryService.getCategoryById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
                book.setCategory(category);
            }

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
            return "admin/edit_book";
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
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            return "admin/edit_book";
        }

        try {
            Book existingBook = bookService.getBookById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));

            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setPrice(bookDetails.getPrice());
            existingBook.setDescription(bookDetails.getDescription());

            // FIX LỖI VALIDATION: Thiết lập Category trước khi gọi Service
            if (categoryId != null && categoryId > 0) {
                Category category = categoryService.getCategoryById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
                existingBook.setCategory(category);
            } else {
                existingBook.setCategory(null);
            }

            bookService.updateBook(id, existingBook, imageFile);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sách thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi cập nhật sách: " + e.getMessage());
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            e.printStackTrace();
            return "admin/edit_book";
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
        try {
            List<Book> searchResults = bookService.searchBooks(keyword.trim());
            model.addAttribute("books", getSafeList(searchResults));
            model.addAttribute("keyword", keyword.trim());
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            model.addAttribute("cartItems", getCartItemsWithQuantity());

            if (searchResults.isEmpty()) {
                model.addAttribute("message", "Không tìm thấy sách nào với từ khóa: '" + keyword.trim() + "'");
            }
            return "search_results";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tìm kiếm: " + e.getMessage());
            model.addAttribute("books", List.of());
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            e.printStackTrace();
            return "search_results";
        }
    }

    @GetMapping("/category/{id}")
    public String viewBooksByCategory(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Category category = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

            List<Book> books = bookService.getBooksByCategory(id);
            model.addAttribute("books", getSafeList(books));
            model.addAttribute("category", category);
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            model.addAttribute("cartItems", getCartItemsWithQuantity());
            model.addAttribute("pageTitle", "Danh mục: " + category.getName());
            return "books_by_category";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi tải danh mục: " + e.getMessage());
            return "redirect:/books";
        }
    }

    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes ra) {
        try {
            cartService.add(id, quantity);
            ra.addFlashAttribute("success", "Đã thêm vào giỏ hàng!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        try {
            model.addAttribute("cartItems", getCartItemsWithQuantity());
            model.addAttribute("total", calculateTotal());
            model.addAttribute("categories", getSafeList(categoryService.getAllCategories()));
            return "cart";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tải giỏ hàng: " + e.getMessage());
            return "cart";
        }
    }

    @GetMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cartService.remove(id);
            ra.addFlashAttribute("success", "Đã xóa khỏi giỏ!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi xóa: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/clear-cart")
    public String clearCart(RedirectAttributes ra) {
        try {
            cartService.clear();
            ra.addFlashAttribute("success", "Đã xóa toàn bộ giỏ!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi xóa giỏ: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String showCheckoutForm(Model model, RedirectAttributes ra) {
        if (cartService.getCart().isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng của bạn đang trống!");
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", getCartItemsWithQuantity());
        model.addAttribute("total", calculateTotal());

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("shippingAddress") String shippingAddress,
            RedirectAttributes ra) {

        if (cartService.getCart().isEmpty()) {
            ra.addFlashAttribute("error", "Không thể đặt hàng vì giỏ hàng trống!");
            return "redirect:/cart";
        }

        try {
            Long newOrderId = orderService.createOrder(
                    getCartItemsWithQuantity(),
                    customerName,
                    shippingAddress
            );

            cartService.clear();

            ra.addFlashAttribute("success", "Đặt hàng thành công! Mã đơn hàng của bạn là #" + newOrderId);
            return "redirect:/order-success/" + newOrderId;

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi đặt hàng: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-success/{id}")
    public String orderSuccess(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "order_success";
    }

    @GetMapping("/aboutus")
    public String showAboutUsPage() {
        return "aboutus";
    }

    private <T> List<T> getSafeList(List<T> list) {
        return list != null ? list : List.of();
    }

    private List<CartItem> getCartItemsWithQuantity() {
        List<CartItem> items = new ArrayList<>();
        Map<Long, Integer> cartMap = cartService.getCart();

        if (cartMap == null) {
            return items;
        }

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            bookService.getBookById(entry.getKey()).ifPresent(book -> {
                items.add(new CartItem(book, entry.getValue()));
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