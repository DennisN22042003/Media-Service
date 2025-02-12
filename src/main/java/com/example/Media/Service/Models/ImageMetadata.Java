package com.example.Media.Service.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "image_Metadate")
public class ImageFile {

    @Id
    private String id;
    private String fileName;
    private String bucketName;
    private String uploadedBy;
    private LocalDateTime uploadedAt;

    // Constructor
    public ImageFile(id, fileName, bucketName, uploadedBy, uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.bucketName = bucketName;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public String getFileName() {
        return fileName;
    }
    public String getBucketName() {
        return bucketName;
    }
    public String getUploadedBy() {
        return uploadedBy;
    }
    public String getUploadedAt() {
        return uploadedAt;
    }
}