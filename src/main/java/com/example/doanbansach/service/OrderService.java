package com.example.doanbansach.service;

import com.example.doanbansach.dto.CartItem;
import com.example.doanbansach.Entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    // Phương thức mà BookController đang gọi (sửa lỗi chính)
    Long createOrder(List<CartItem> cartItems, String customerName, String shippingAddress);

    // Các phương thức để quản lý đơn hàng (cho OrderController)
    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    Order updateOrderStatus(Long id, String status);
}