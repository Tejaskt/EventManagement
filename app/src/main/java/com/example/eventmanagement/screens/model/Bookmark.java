package com.example.eventmanagement.screens.model;

import com.google.firebase.firestore.Exclude;

public class Bookmark {
    @Exclude
    private String id;
    private String userId;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String eventImageUrl;
    private long bookmarkedAt;

    public Bookmark() {
        // Required empty constructor for Firestore
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public long getBookmarkedAt() {
        return bookmarkedAt;
    }

    public void setBookmarkedAt(long bookmarkedAt) {
        this.bookmarkedAt = bookmarkedAt;
    }
}