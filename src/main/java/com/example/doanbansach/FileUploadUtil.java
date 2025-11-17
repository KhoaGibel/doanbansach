package com.example.doanbansach;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    // Thư mục lưu ảnh (tự động tạo nếu chưa có)
    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads";

    public static String saveFile(String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = multipartFile.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";

        String newFileName = fileName + extension;
        Path filePath = uploadPath.resolve(newFileName);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Trả về đường dẫn để hiển thị trên web
        return "/images/uploads/" + newFileName;
    }

    public static void deleteOldFile(String oldFilePath) {
        if (oldFilePath != null && oldFilePath.startsWith("/images/uploads/")) {
            try {
                Path path = Paths.get("src/main/resources/static" + oldFilePath);
                Files.deleteIfExists(path);
            } catch (Exception e) {
                System.out.println("Không xóa được ảnh cũ: " + e.getMessage());
            }
        }
    }
}