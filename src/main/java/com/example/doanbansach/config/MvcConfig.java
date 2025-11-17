package com.example.doanbansach.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String urlPath = "/uploads/**";

        // Chuyển đường dẫn của bạn sang định dạng tuyệt đối
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
        String physicalPath = "file:/" + absolutePath.replace("\\", "/") + "/"; // Sửa lỗi path Windows

        // THÊM DÒNG NÀY ĐỂ DEBUG
        System.out.println("DEBUG: Physical Image Path being served: " + physicalPath);

        registry.addResourceHandler(urlPath)
                .addResourceLocations(physicalPath);
    }
}