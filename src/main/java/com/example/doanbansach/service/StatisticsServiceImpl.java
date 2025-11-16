package com.example.doanbansach.service;

import com.example.doanbansach.Repository.OrderDetailRepository;
import com.example.doanbansach.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public double getTotalRevenue() {
        // Chỉ tính doanh thu từ các đơn hàng "HOÀN_TẤT"
        return orderRepository.findAll().stream()
                .filter(order -> "HOÀN_TẤT".equals(order.getStatus()))
                .mapToDouble(order -> order.getTotalPrice() != null ? order.getTotalPrice() : 0.0)
                .sum();
    }

    @Override
    public long getTotalOrdersSold() {
        // Chỉ đếm các đơn hàng "HOÀN_TẤT"
        return orderRepository.findAll().stream()
                .filter(order -> "HOÀN_TẤT".equals(order.getStatus()))
                .count();
    }

    @Override
    public long getTotalBooksSold() {
        // Đếm tổng số lượng sách trong các đơn hàng đã "HOÀN_TẤT"
        return orderDetailRepository.findAll().stream()
                .filter(detail -> "HOÀN_TẤT".equals(detail.getOrder().getStatus()))
                .mapToLong(detail -> detail.getQuantity())
                .sum();
    }

    @Override
    public Map<String, Double> getRevenueByCategory() {
        Map<String, Double> revenueMap = new HashMap<>();

        orderDetailRepository.findAll().stream()
                .filter(detail -> "HOÀN_TẤT".equals(detail.getOrder().getStatus()) && detail.getBook().getCategory() != null)
                .forEach(detail -> {
                    String categoryName = detail.getBook().getCategory().getName();
                    double revenue = detail.getPrice() * detail.getQuantity();

                    revenueMap.merge(categoryName, revenue, Double::sum);
                });

        return revenueMap;
    }
}