package com.example.doanbansach.service;

import java.util.Map;

public interface StatisticsService {


    double getTotalRevenue();


    long getTotalOrdersSold();


    long getTotalBooksSold();


    Map<String, Double> getRevenueByCategory();
}