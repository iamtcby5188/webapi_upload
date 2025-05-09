package com.webapi.model;

public class FileInfo {
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

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}