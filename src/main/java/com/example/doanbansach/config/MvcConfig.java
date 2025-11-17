package com.example.doanbansach.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Đường dẫn URL mà trình duyệt sẽ gọi
        String urlPath = "/uploads/**";

        // Đường dẫn tuyệt đối đến thư mục trên ổ cứng (phải có "file:")
        String physicalPath = "file:" + Paths.get(uploadDir).toAbsolutePath().toString() + "/";

        registry.addResourceHandler(urlPath)
                .addResourceLocations(physicalPath);
    }
}