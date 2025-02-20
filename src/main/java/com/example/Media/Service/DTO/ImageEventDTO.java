package com.example.Media.Service.DTO;

import java.io.Serializable;

public class ImageEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String eventId;
    private String imageUrl;

    public ImageEventDTO(String eventId, String imageUrl, String userId) {
        this.eventId = eventId;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
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
