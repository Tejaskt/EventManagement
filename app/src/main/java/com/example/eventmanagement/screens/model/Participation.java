package com.example.eventmanagement.screens.model;

import com.google.firebase.firestore.Exclude;

public class Participation {
    @Exclude
    private String id;
    private String userId;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private long participationDate;

    public Participation() {
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

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public long getParticipationDate() {
        return participationDate;
    }

    public void setParticipationDate(long participationDate) {
        this.participationDate = participationDate;
    }
}