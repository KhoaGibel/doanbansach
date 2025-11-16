package com.example.doanbansach.Controller;

import com.example.doanbansach.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public String showStatistics(Model model) {
        model.addAttribute("totalRevenue", statisticsService.getTotalRevenue());
        model.addAttribute("totalOrdersSold", statisticsService.getTotalOrdersSold());
        model.addAttribute("totalBooksSold", statisticsService.getTotalBooksSold());
        model.addAttribute("revenueByCategory", statisticsService.getRevenueByCategory());

        return "admin/statistics";
    }
}