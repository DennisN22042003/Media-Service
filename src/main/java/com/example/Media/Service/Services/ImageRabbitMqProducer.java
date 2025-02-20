package com.example.Media.Service.Services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

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

    // Send Image Event with eventId, imageUrl and userId as strings to RabbitMQ
    public void sendImageEvent(String eventId, String imageUrl, String userId) {
        // Create an ImageEventDTO object
        ImageEventDTO metadata = new ImageEventDTO(eventId, imageUrl, userId);
        metadata.getEventId();
        metadata.getImageUrl();
        metadata.getUserId();
        // Log the ImageEventDTO before serialization
        System.out.println("📤 Preparing to send Image Event: " + metadata.getImageUrl() + " for Event: " + metadata.getEventId() + " for User: " + metadata.getUserId());

        // Serialize and send the ImageMetadata object to RabbitMQ
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, metadata);

        // Log the serialized message sent to RabbitMQ
        System.out.println("📤 Sent Image Event (Serialized): " + metadata.getImageUrl() + " for Event: " + metadata.getEventId() + " for User: " + metadata.getUserId());
    }
}
