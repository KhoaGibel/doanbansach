package com.example.doanbansach.service;

import com.example.doanbansach.Entity.Order;
import com.example.doanbansach.Entity.OrderDetail;
import com.example.doanbansach.dto.CartItem;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // SỬA LỖI: THÊM 2 THAM SỐ phone VÀ paymentMethod
    Long createOrder(List<CartItem> cartItems,
                     String customerName,
                     String shippingAddress,
                     String phone,
                     String paymentMethod) throws Exception;

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    Order updateOrderStatus(Long id, String status);

    // Giả định bạn đã có hàm này trong OrderController
    List<OrderDetail> getOrderDetails(Long orderId);
}