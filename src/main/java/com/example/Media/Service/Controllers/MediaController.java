package com.example.Media.Service.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Media.Service.Services.GCSstorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final GCSstorageService gcsStorageService;

    public MediaController(GCSstorageService gcsStorageService) {
        this.gcsStorageService = gcsStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = gcsStorageService.uploadImage(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String fileName) {
        return ResponseEntity.ok(gcsStorageService.getFileURL(fileName));
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        gcsStorageService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully!");
    }

    // Future work: Implement a method to download a file from the bucket
    // Future work: Implement a method to update a file in the bucket
}