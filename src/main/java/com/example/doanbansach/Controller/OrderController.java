package com.example.doanbansach.Controller;

import com.example.doanbansach.Entity.Order;
import com.example.doanbansach.service.OrderService;
import com.example.doanbansach.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired(required = false)
    private CartService cartService;

    @GetMapping("/orders")
    public String listAllOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/order_list";
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Order order = orderService.getOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));
            model.addAttribute("order", order);
            return "admin/order_detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @GetMapping("/orders/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID đơn hàng không tồn tại: " + id));
        model.addAttribute("order", order);
        model.addAttribute("statuses", List.of("CHỜ_XỬ_LÝ", "ĐANG_GIAO", "HOÀN_TẤT", "ĐÃ_HỦY"));
        return "admin/edit_order";
    }

    @PostMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") String newStatus,
                                    RedirectAttributes ra) {
        try {
            orderService.updateOrderStatus(id, newStatus);
            ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng #" + id + " thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + id;
    }
}