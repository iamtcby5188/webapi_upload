package com.webapi.model;

public class FileResponse {
    private String message;
    private String fileUrl;

    public FileResponse(String message, String fileUrl) {
        this.message = message;
        this.fileUrl = fileUrl;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}