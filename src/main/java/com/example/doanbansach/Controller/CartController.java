package com.example.doanbansach.Controller; // <<< Thay bằng package của bạn nhé!

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    // Thêm sách vào giỏ hàng
    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        // Lấy giỏ hàng từ session (dạng Map: id sách -> số lượng)
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Tăng số lượng sách (nếu đã có thì +1, chưa có thì bắt đầu từ 1)
        cart.put(id, cart.getOrDefault(id, 0) + 1);

        // Lưu lại vào session
        session.setAttribute("cart", cart);

        // Thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Đã thêm vào giỏ hàng thành công!");

        // Quay lại trang chi tiết sách
        return "redirect:/books/detail/" + id;
    }

    // Xem giỏ hàng
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");

        // Nếu chưa có giỏ hàng thì tạo mới (tránh null)
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Đưa giỏ hàng vào model để hiển thị ở cart.html
        model.addAttribute("cart", cart);

        // Tính tổng số lượng sản phẩm trong giỏ (hiển thị số trên icon giỏ hàng)
        int totalItems = cart.values().stream().mapToInt(Integer::intValue).sum();
        model.addAttribute("totalItems", totalItems);

        return "cart"; // → templates/cart.html
    }

    // Xóa sách khỏi giỏ hàng (tùy chọn thêm)
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(id);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // Cập nhật số lượng (tùy chọn)
    @GetMapping("/cart/update/{id}/{quantity}")
    public String updateQuantity(@PathVariable Long id,
                                 @PathVariable Integer quantity,
                                 HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null && quantity != null && quantity > 0) {
            cart.put(id, quantity);
        } else if (cart != null && quantity != null && quantity <= 0) {
            cart.remove(id);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }
}