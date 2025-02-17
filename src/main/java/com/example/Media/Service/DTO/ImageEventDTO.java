package com.example.Media.Service.DTO;

public class ImageEventDTO {
    private String eventId;
    private String imageUrl;

    public ImageEventDTO(String eventId, String imageUrl) {
        this.eventId = eventId;
        this.imageUrl = imageUrl;
    }

    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
