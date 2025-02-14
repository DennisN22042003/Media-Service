package com.example.Media.Service.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class ImageKafkaProducer {
    private static final String TOPIC = "image-media-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendImageEvent(String eventId, String imageId) {
        String message = eventId + ":" + imageId;
        kafkaTemplate.send(TOPIC, message);
        System.out.println("📤 Sent Kafka event -> " + message);
    }
}
