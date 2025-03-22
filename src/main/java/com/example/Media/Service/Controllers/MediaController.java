package com.example.Media.Service.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Media.Service.Services.GCSstorageService;
import com.example.Media.Service.Services.ImageRabbitMqProducer;
import com.example.Media.Service.Models.ImageMetadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private ImageRabbitMqProducer imageRabbitMqProducer;

    @Autowired
    private GCSstorageService gcsStorageService;

    public MediaController(GCSstorageService gcsStorageService, ImageRabbitMqProducer imageRabbitMqProducer) {
        this.imageRabbitMqProducer = imageRabbitMqProducer;
        this.gcsStorageService = gcsStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("eventId") String eventId, @RequestParam("userId") String userId) {

        // Validate Event
        if (!gcsStorageService.isEventValid(eventId)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid Event ID");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        // Upload Image to GCS & Save metadata to MongoDB
        try {
            ImageMetadata metadata = gcsStorageService.uploadImage(file, eventId, userId);
            String fileUrl = metadata.getUrl();
            imageRabbitMqProducer.sendImageEvent(eventId, fileUrl, userId);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Photo uploaded, image event sent to Events Service via RabbitMQ");
            successResponse.put("imageUrl", fileUrl);

            return ResponseEntity.ok(successResponse);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
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