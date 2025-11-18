package com.example.doanbansach.Controller;

import com.example.doanbansach.dto.CartItem;
import com.example.doanbansach.service.BookService;
import com.example.doanbansach.service.CartService;
import com.example.doanbansach.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    @Autowired
    private OrderService orderService; // Cần cho logic đặt hàng

    // --- HÀM HỖ TRỢ (Lấy Chi tiết Sách cho View) ---

    // Hàm này phải được đặt ở đây vì CartController cần nó để hiển thị giỏ hàng
    private List<CartItem> getCartItemsWithDetails() {
        List<CartItem> items = new ArrayList<>();
        Map<Long, Integer> cartMap = cartService.getCart(); // Lấy Map<ID, Quantity>

        if (cartMap == null) return items;

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();

            // Dùng BookService để tìm sách
            bookService.getBookById(bookId).ifPresent(book -> {
                items.add(new CartItem(book, quantity)); // Tạo DTO CartItem
            });
        }
        return items;
    }

    private double calculateTotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(item -> item.book().getPrice() * item.quantity())
                .sum();
    }

    // --- HÀM ADD TO CART ---
    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {

        cartService.add(id, quantity);
        redirectAttributes.addFlashAttribute("success", "Đã thêm vào giỏ hàng thành công!");

        // Chuyển hướng đến /cart
        return "redirect:/cart";
    }

    // --- HÀM XEM GIỎ HÀNG ---
    @GetMapping("/cart")
    public String viewCart(Model model) {

        List<CartItem> cartItems = getCartItemsWithDetails();
        double totalPrice = calculateTotal(cartItems);

        model.addAttribute("cartItems", cartItems); // Gửi List DTOs sang view
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalItems", cartService.getTotalItems()); // Lấy tổng số lượng

        return "cart"; // → templates/cart.html
    }

    // --- HÀM CẬP NHẬT SỐ LƯỢNG ---
    @GetMapping("/cart/update/{id}")
    public String updateQuantity(@PathVariable Long id,
                                 @RequestParam("quantity") Integer quantity) {

        cartService.update(id, quantity);
        return "redirect:/cart";
    }

    // --- HÀM XÓA SÁCH KHỎI GIỎ ---
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.remove(id);
        return "redirect:/cart";
    }

    // --- HÀM THANH TOÁN (GET) ---
    @GetMapping("/checkout")
    public String showCheckoutForm(Model model, RedirectAttributes ra) {
        List<CartItem> cartItems = getCartItemsWithDetails();

        if (cartItems.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng của bạn đang trống!");
            return "redirect:/cart";
        }

        double totalPrice = calculateTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalItems", cartService.getTotalItems());

        return "checkout";
    }

    // --- HÀM ĐẶT HÀNG (POST) ---
    // HÀM NÀY PHẢI CÓ THAM SỐ phone VÀ paymentMethod
    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("phone") String phone,
            @RequestParam("paymentMethod") String paymentMethod,
            RedirectAttributes ra) {

        List<CartItem> cartItems = getCartItemsWithDetails();
        if (cartItems.isEmpty()) {
            ra.addFlashAttribute("error", "Không thể đặt hàng vì giỏ hàng trống!");
            return "redirect:/cart";
        }

        try {
            // GỌI HÀM createOrder CỦA OrderService
            Long newOrderId = orderService.createOrder(
                    cartItems,
                    customerName,
                    shippingAddress,
                    phone, // Đã thêm
                    paymentMethod // Đã thêm
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
}