package com.example.Media.Service.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageMetadataRepository extends MongoRepository<ImageFile, String> {
    // Future work: Implement methods to query the database for image metadata
    // Future work: Implement methods to update image metadata
    // Future work: Implement methods to delete image metadata
}