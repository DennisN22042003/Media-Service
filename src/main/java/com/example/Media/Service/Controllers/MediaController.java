package com.example.Media.Service.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Media.Service.Services.GCSstorageService;
import com.example.Media.Service.Services.ImageKafkaProducer;
import com.example.Media.Service.Models.ImageMetadata;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private ImageKafkaProducer imageKafkaProducer;

    @Autowired
    private GCSstorageService gcsStorageService;

    public MediaController(GCSstorageService gcsStorageService) {
        this.gcsStorageService = gcsStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("eventId") String eventId) {

        // Validate Event
        if (!gcsStorageService.isEventValid(eventId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Event ID");
        }
        
        // Upload Image to GCS & Save metadata to MongoDB
        try {
            ImageMetadata metadata = gcsStorageService.uploadImage(file);
            String fileUrl = metadata.getUrl(); // Assuming getUrl() method exists in ImageMetadata
            String imageId = metadata.getId();
            imageKafkaProducer.sendImageEvent(eventId, imageId); // Send Kafka event to Events Micro-service
            return ResponseEntity.ok("Photo uploaded, image event sent to Kafka" + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<String> getFileUrl(@RequestParam String fileName) {
        return ResponseEntity.ok(gcsStorageService.getFileURL(fileName));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        gcsStorageService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully!");
    }

    // Future work: Implement a method to download a file from the bucket
    // Future work: Implement a method to update a file in the bucket
    // Future work: Implement relevant methods related to users adding videos to their montages/events
}