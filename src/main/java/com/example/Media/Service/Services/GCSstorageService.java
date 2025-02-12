package com.example.Media.Service.Services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GCSstorageService {
    
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    public GCSstorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();

        Blob blob = storage.create(blobInfo, file.getBytes());

        return blob.getMediaLink(); // Public URL if ACL allows it
    }

    public String getFileURL(String fileName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    public void deleteFile(String fileName) {
        storage.delete(bucketName, fileName);
    }

}
