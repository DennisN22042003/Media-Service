package com.example.Media.Service.Services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Media.Service.Configs.RabbitMQConfig;
import com.example.Media.Service.Models.ImageMetadata;

@Service
public class ImageRabbitMqProducer {
    
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ImageRabbitMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendImageEvent(String eventId, String imageId) {
        // ImageEvent imageEvent = new ImageEvent(eventId, imageUrl);
        ImageMetadata metadata = new ImageMetadata(eventId, imageId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, metadata);
        System.out.println("Send image event for eventId: " + eventId);
    }
}
