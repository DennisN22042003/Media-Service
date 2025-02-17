package com.example.Media.Service.Services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Media.Service.Configs.RabbitMQConfig;
import com.example.Media.Service.DTO.ImageEventDTO;

@Service
public class ImageRabbitMqProducer {
    
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ImageRabbitMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendImageEvent(String eventId, String imageUrl) {
        ImageEventDTO metadata = new ImageEventDTO(eventId, imageUrl);
        metadata.getEventId();
        metadata.getImageUrl();

        // Send the ImageMetadata object to RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, metadata);
        System.out.println("📤 Sent Image Event: " + metadata.getImageUrl() + " for Event: " + metadata.getEventId());
    }
}
