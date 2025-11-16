package com.example.doanbansach.service;

import com.example.doanbansach.dto.CartItem;
import com.example.doanbansach.Entity.Order;
import com.example.doanbansach.Entity.OrderDetail;
import com.example.doanbansach.Repository.OrderRepository;
import com.example.doanbansach.Repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional // Đảm bảo tất cả cùng thành công hoặc thất bại
    public Long createOrder(List<CartItem> cartItems, String customerName, String shippingAddress) {

        // 1. Tạo đối tượng Order
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CHỜ_XỬ_LÝ");

        // Tính tổng tiền
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.book().getPrice() * item.quantity())
                .sum();
        order.setTotalPrice(totalPrice);

        // 2. Lưu Order để lấy ID
        Order savedOrder = orderRepository.save(order);

        // 3. Tạo và lưu các OrderDetail (các món hàng trong đơn)
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setBook(item.book());
            detail.setQuantity(item.quantity());
            detail.setPrice(item.book().getPrice());

            orderDetailRepository.save(detail);
        }

        return savedOrder.getId();
    }

    // === Các phương thức quản lý (cho OrderController sau này) ===

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}