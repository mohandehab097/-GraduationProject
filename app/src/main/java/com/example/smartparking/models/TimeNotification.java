package com.example.smartparking.models;

public class TimeNotification {

    private boolean limitTimeNotification;
    private boolean nearTimeNotification;

    public TimeNotification() {

    }

    public boolean isLimitTimeNotification() {
        return limitTimeNotification;
    }

    public void setLimitTimeNotification(boolean limitTimeNotification) {
        this.limitTimeNotification = limitTimeNotification;
    }

    public boolean isNearTimeNotification() {
        return nearTimeNotification;
    }

    public void setNearTimeNotification(boolean nearTimeNotification) {
        this.nearTimeNotification = nearTimeNotification;
    }
}
