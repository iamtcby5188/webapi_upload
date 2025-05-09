package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads";

    public FileUploadController() {
        // 创建上传目录
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("请选择要上传的文件");
            }

            // 获取文件名
            String fileName = file.getOriginalFilename();
            
            // 确保文件名不为空
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body("文件名不能为空");
            }

            // 构建文件保存路径
            Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
            
            // 保存文件
            Files.copy(file.getInputStream(), path);

            return ResponseEntity.ok("文件上传成功：" + fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("文件上传失败：" + e.getMessage());
        }
    }
}