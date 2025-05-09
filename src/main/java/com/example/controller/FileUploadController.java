package com.example.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
public class FileUploadController {

    private static final String UPLOAD_DIR = "d:\\uploads";

    public FileUploadController() {
        // 创建上传目录
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("API入口 - /upload - 开始处理文件上传请求");
        try {
            if (file.isEmpty()) {
                System.out.println("API出口 - /upload - 文件为空，上传失败");
                return ResponseEntity.badRequest().body(new FileResponse("请选择要上传的文件", null));
            }
            // 获取文件名和扩展名
            String originalFileName = file.getOriginalFilename();
            System.out.println(originalFileName);
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 构建文件保存路径
            Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
            
            // 保存文件
            Files.copy(file.getInputStream(), path);

            // 构建文件访问URL
            String fileUrl = "/api/storage/files/" + fileName;
            
            System.out.println("API出口 - /upload - 文件上传成功：" + fileName);
            return ResponseEntity.ok(new FileResponse("文件上传成功", fileUrl));
        } catch (IOException e) {
            System.out.println("API出口 - /upload - 文件上传失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(new FileResponse("文件上传失败：" + e.getMessage(), null));
        }
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        System.out.println("API入口 - /files/{fileName} - 开始获取文件：" + fileName);
        try {
            Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
            byte[] content = Files.readAllBytes(path);
            
            System.out.println("API出口 - /files/{fileName} - 文件获取成功：" + fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (IOException e) {
            System.out.println("API出口 - /files/{fileName} - 文件获取失败：" + fileName);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<FileResponse> deleteFile(@PathVariable String fileName) {
        System.out.println("API入口 - /files/{fileName} - 开始删除文件：" + fileName);
        try {
            Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
            boolean deleted = Files.deleteIfExists(path);
            
            if (deleted) {
                System.out.println("API出口 - /files/{fileName} - 文件删除成功：" + fileName);
                return ResponseEntity.ok(new FileResponse("文件删除成功", null));
            } else {
                System.out.println("API出口 - /files/{fileName} - 文件不存在：" + fileName);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            System.out.println("API出口 - /files/{fileName} - 文件删除失败：" + fileName + ", 错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(new FileResponse("文件删除失败：" + e.getMessage(), null));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> listFiles() {
        System.out.println("API入口 - /files - 开始获取文件列表");
        try {
            List<FileInfo> files = new ArrayList<>();
            File directory = new File(UPLOAD_DIR);
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
            
            System.out.println("API出口 - /files - 文件列表获取成功，共" + files.size() + "个文件");
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            System.out.println("API出口 - /files - 文件列表获取失败：" + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

class FileResponse {
    private String message;
    private String fileUrl;

    public FileResponse(String message, String fileUrl) {
        this.message = message;
        this.fileUrl = fileUrl;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}

class FileInfo {
    private String fileName;
    private String url;
    private long size;
    private long lastModified;

    public FileInfo(String fileName, String url, long size, long lastModified) {
        this.fileName = fileName;
        this.url = url;
        this.size = size;
        this.lastModified = lastModified;
    }

    // Getters and setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}