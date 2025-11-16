package com.example.doanbansach.service;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {

    private final Map<Long, Integer> cart = new HashMap<>();

    // Thêm sách vào giỏ
    public void add(Long bookId, int quantity) {
        if (bookId != null && quantity > 0) {
            cart.put(bookId, cart.getOrDefault(bookId, 0) + quantity);
        }
    }

    // Xóa sách khỏi giỏ
    public void remove(Long bookId) {
        if (bookId != null) {
            cart.remove(bookId);
        }
    }

    // Lấy giỏ hàng (Map<bookId, quantity>)
    public Map<Long, Integer> getCart() {
        return new HashMap<>(cart); // Trả về bản sao để an toàn
    }

    // Xóa toàn bộ giỏ
    public void clear() {
        cart.clear();
    }

    // Đếm tổng số món trong giỏ
    public int getTotalItems() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }
}