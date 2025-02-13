package com.example.Media.Service.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

import com.example.Media.Service.Models.ImageMetadata; // Ensure this class exists in the specified package

@Repository
public interface ImageMetadataRepository extends MongoRepository<ImageMetadata, String> {
    // Future work: Implement methods to query the database for image metadata
    // Future work: Implement methods to update image metadata
    // Future work: Implement methods to delete image metadata

    @Query("{ 'fileName' : ?0 }")
    long deleteByFileName(String fileName);

    @Query("{ 'fileName' : ?0 }")
    Optional<ImageMetadata> findByFileName(String fileName);
}