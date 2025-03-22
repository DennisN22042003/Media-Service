package com.example.Media.Service.Services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Optional;
import java.time.LocalDateTime;

import com.example.Media.Service.Repositories.ImageMetadataRepository;
import com.example.Media.Service.Models.ImageMetadata;
import com.example.Media.Service.DTO.ImageEventDTO;

@Service
public class GCSstorageService {
    
    private final Storage storage;
    private final ImageMetadataRepository imageMetadataRepository; // Inject the MongoDB repository
    private final RestTemplate restTemplate = new RestTemplate();
    private final String EVENT_SERVICE_URL = "http://localhost:8082/api/events";

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

    // Before storing the image, check if the event exists
    public boolean isEventValid(String eventId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(EVENT_SERVICE_URL + "/validate/" + eventId, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false; // Event does not exist
        }
    }

    public ImageMetadata uploadImage(MultipartFile file, String eventId, String userId) throws IOException {
        // Generate a unique file name
        // String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        // String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        // BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();

        // Generate a unique image ID (UUID)
        String imageId = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // Use the imageId as the file name
        String fileName = imageId + extension;

        // Determine Content-Type based on file extension
        String contentType = determineContentType(extension);

        // Create BlobInfo with Content-Type
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(contentType)
                .build();

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

        // Metadata to be sent to Events Service via RabbitMQ(use DTO to avoid exposing schemas outside this Service)
        ImageEventDTO DTOmetadata = new ImageEventDTO(eventId, fileUrl, userId);
        DTOmetadata.setUserId(userId);
        DTOmetadata.setEventId(eventId);
        DTOmetadata.setImageUrl(fileUrl);
        

        return imageMetadataRepository.save(metadata);
    }

    // Helper method to determine Content-Type
    private String determineContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    /*public void linkImageToEvent(String eventId, String imageId) {
        String url = EVENT_SERVICE_URL + "/" + eventId + "/add-image";

        try {
            restTemplate.postForEntity(url, imageId, String.class);
            System.out.println("✅ Image linked to event with imageId: " + imageId);
        } catch (Exception e) {
            System.err.println("❌ Failed to link media to event: " + e.getMessage());
        }
    } */

    public String getFileURL(String imageId) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, imageId);
    }

    @Transactional
    public void deleteFile(String imageId) {
        System.out.println("🔹 Attempting to delete file with imageId: " + imageId);

        // Check if the file exists in GCS before deletion
        Blob blob = storage.get(bucketName, imageId);
        if (blob == null) {
            throw new RuntimeException("⚠️ Image File not found in GCS: " + imageId);
        }

        // Delete Image File from GCS
        boolean deleted = storage.delete(bucketName, imageId);
        if (!deleted) {
            throw new RuntimeException("❌ Failed to delete image file from GCS: " + imageId);
        }
        System.out.println("✅ Image File deleted from GCS");

        /*
         * // Check if metadata exists before deleting
            Optional<ImageMetadata> metadata = imageMetadataRepository.findByFileName(fileName);
            if (metadata.isEmpty()) {
                throw new RuntimeException("⚠️ Metadata not found in MongoDB: " + fileName);
            }
         */

        // Delete metadata from MongoDB
        try {
            imageMetadataRepository.deleteByFileName(imageId);
            System.out.println("✅ Image File metadata deleted from MongoDB");
        } catch (Exception e) {
            // Handle failure by logging and potentially retrying later
            throw new RuntimeException("❌ Failed to delete Image File Metadata from MongoDB");
        }
    }
}
