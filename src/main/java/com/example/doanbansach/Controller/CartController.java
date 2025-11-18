package com.example.doanbansach.Controller;

import com.example.doanbansach.dto.CartItem;
import com.example.doanbansach.service.BookService;
import com.example.doanbansach.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    // Thêm sách vào giỏ hàng
    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {

        // SỬA: Dùng Service để thêm sách
        cartService.add(id, quantity);

        redirectAttributes.addFlashAttribute("message", "Đã thêm vào giỏ hàng thành công!");

        // Chuyển hướng đến /cart theo yêu cầu của bạn
        return "redirect:/cart";
    }

    // Xem giỏ hàng
    @GetMapping("/cart")
    public String viewCart(Model model) {

        Map<Long, Integer> cartMap = cartService.getCart();

        // Lấy chi tiết sách cho giỏ hàng
        List<CartItem> cartItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();

            // Dùng BookService để tìm sách và tạo CartItem
            bookService.getBookById(bookId).ifPresent(book -> {
                cartItems.add(new CartItem(book, quantity));
            });
        }

        // Tính tổng tiền
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.book().getPrice() * item.quantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalItems", cartService.getTotalItems());

        return "cart"; // → templates/cart.html
    }

    // Xóa sách khỏi giỏ hàng
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.remove(id);
        return "redirect:/cart";
    }

    // Cập nhật số lượng
    @GetMapping("/cart/update/{id}")
    public String updateQuantity(@PathVariable Long id,
                                 @RequestParam("quantity") Integer quantity) {

        // SỬA: Dùng hàm update() mới của Service
        cartService.update(id, quantity);
        return "redirect:/cart";
    }
}