package com.webapi.controller;

import com.webapi.model.FileResponse;
import com.webapi.model.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/storage")
public class FileUploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Value("${upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        logger.info("初始化上传目录: {}", uploadDir);
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.info("创建上传目录成功");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("开始处理文件上传请求");
        try {
            if (file.isEmpty()) {
                logger.warn("上传文件为空");
                return ResponseEntity.badRequest().body(new FileResponse("请选择要上传的文件", null));
            }
            
            String originalFileName = file.getOriginalFilename();
            logger.debug("原始文件名: {}", originalFileName);
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path path = Paths.get(uploadDir + File.separator + fileName);
            Files.copy(file.getInputStream(), path);
            String fileUrl = "/api/storage/files/" + fileName;
            
            logger.info("文件上传成功: {}", fileName);
            return ResponseEntity.ok(new FileResponse("文件上传成功", fileUrl));
        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new FileResponse("文件上传失败：" + e.getMessage(), null));
        }
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        try {
            Path path = Paths.get(uploadDir + File.separator + fileName);
            byte[] content = Files.readAllBytes(path);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<FileResponse> deleteFile(@PathVariable String fileName) {
        try {
            Path path = Paths.get(uploadDir + File.separator + fileName);
            boolean deleted = Files.deleteIfExists(path);
            
            if (deleted) {
                return ResponseEntity.ok(new FileResponse("文件删除成功", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new FileResponse("文件删除失败：" + e.getMessage(), null));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> listFiles() {
        try {
            List<FileInfo> files = new ArrayList<>();
            File directory = new File(uploadDir);
            File[] fileList = directory.listFiles();
            
            if (fileList != null) {
                for (File file : fileList) {
                    files.add(new FileInfo(
                        file.getName(),
                        "/api/storage/files/" + file.getName(),
                        Files.size(file.toPath()),
                        Files.getLastModifiedTime(file.toPath()).toMillis()
                    ));
                }
            }
            
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}