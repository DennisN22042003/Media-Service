package com.example.Media.Service.Services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Media.Service.Configs.RabbitMQConfig;
import com.example.Media.Service.Models.ImageMetadata;

@Service
public class ImageRabbitMqProducer {
    
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ImageRabbitMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendImageEvent(String eventId, String imageId, String imageUrl) {
        // ImageEvent imageEvent = new ImageEvent(eventId, imageUrl);
        ImageMetadata metadata = new ImageMetadata();
        metadata.setId(imageId);
        metadata.setEventId(eventId);
        metadata.setUrl(imageUrl);

        // Send the ImageMetadata object to RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, metadata);
        System.out.println("Send image event for eventId: " + eventId);
    }
}
