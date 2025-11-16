package com.example.doanbansach.dto;

import com.example.doanbansach.Entity.Book;

public record CartItem(Book book, int quantity) {}