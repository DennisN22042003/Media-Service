package com.example.Media.Service.DTO;

import java.io.Serializable;

public class ImageEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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
