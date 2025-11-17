// src/main/java/com/example/doanbansach/controller/AdminController.java

package com.example.doanbansach.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String showDashboard(Model model) {
        // Dữ liệu giả để test giao diện
        model.addAttribute("totalOrders", 156);
        model.addAttribute("totalRevenue", 28500000);
        model.addAttribute("totalCustomers", 89);
        model.addAttribute("totalBooksSold", 320);

        // Biểu đồ: 7 ngày gần nhất
        model.addAttribute("revenueLabels", "['10/11', '11/11', '12/11', '13/11', '14/11', '15/11', '16/11']");
        model.addAttribute("revenueData", "[1200000, 1800000, 2500000, 2200000, 3000000, 2800000, 3500000]");

        // Top 5 sách bán chạy
        model.addAttribute("topBooks", java.util.List.of(
                new BookStat("Lập trình Java", "Nguyễn Văn A", 45, 2250000),
                new BookStat("Spring Boot Cơ Bản", "Trần Thị B", 38, 1900000),
                new BookStat("HTML & CSS", "Lê Văn C", 32, 960000),
                new BookStat("Python cho người mới", "Phạm Thị D", 28, 1400000),
                new BookStat("SQL Server", "Hoàng Văn E", 25, 1250000)
        ));

        return "admin-dashboard"; // → templates/admin-dashboard.html
    }

    // Class nội bộ để hiển thị bảng
    public static class BookStat {
        public String title;
        public String author;
        public int sold;
        public long revenue;

        public BookStat(String title, String author, int sold, long revenue) {
            this.title = title;
            this.author = author;
            this.sold = sold;
            this.revenue = revenue;
        }
    }
}