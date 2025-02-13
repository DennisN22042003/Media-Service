package com.example.Media.Service.Services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Optional;
import java.time.LocalDateTime;

import com.example.Media.Service.Repositories.ImageMetadataRepository;
import com.example.Media.Service.Models.ImageMetadata;

@Service
public class GCSstorageService {
    
    private final Storage storage;
    private final ImageMetadataRepository imageMetadataRepository; // Inject the MongoDB repository

    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    @Autowired
    public GCSstorageService(ImageMetadataRepository imageMetadataRepository) throws IOException  {
        this.imageMetadataRepository = imageMetadataRepository;

        // Authenticate using Application Default Credentials (ADC)
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        storage = StorageOptions.newBuilder()
                                .setCredentials(credentials)
                                .build()
                                .getService();
    }

    public ImageMetadata uploadImage(MultipartFile file) throws IOException {
        // Generate a unique file name
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();

        // Upload the image file to Google Cloud Storage
        Blob blob = storage.create(blobInfo, file.getBytes());

        // Construct file URL
        String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

        // Save metadata in MongoDB
        ImageMetadata metadata = new ImageMetadata();
        metadata.setFileName(fileName);
        metadata.setUrl(fileUrl);
        metadata.setUploadedAt(LocalDateTime.now());
        metadata.setUploadedBy("testUser"); // Replace with actual user info if available

        return imageMetadataRepository.save(metadata);
    }

    public String getFileURL(String fileName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    @Transactional
    public void deleteFile(String fileName) {
        System.out.println("🔹 Attempting to delete file: " + fileName);

        // Decode filename (handles URL encoding like %20 -> space)
        try {
            fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error decoding filename: " + fileName, e);
        }
        System.out.println("✅ Decoded filename: " + fileName);

        // Check if the file exists in GCS before deletion
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            throw new RuntimeException("⚠️ Image File not found in GCS: " + fileName);
        }

        // Delete Image File from GCS
        boolean deleted = storage.delete(bucketName, fileName);
        if (!deleted) {
            throw new RuntimeException("❌ Failed to delete image file from GCS: " + fileName);
        }
        System.out.println("✅ File deleted from GCS.");

        /*
         * // Check if metadata exists before deleting
            Optional<ImageMetadata> metadata = imageMetadataRepository.findByFileName(fileName);
            if (metadata.isEmpty()) {
                throw new RuntimeException("⚠️ Metadata not found in MongoDB: " + fileName);
            }
         */

        // Delete metadata from MongoDB
        try {
            imageMetadataRepository.deleteByFileName(fileName);
            System.out.println("✅ Image File metadata deleted from MongoDB");
        } catch (Exception e) {
            // Handle failure by logging and potentially retrying later
            throw new RuntimeException("❌ Failed to delete Image File Metadata from MongoDB");
        }
    }
}
