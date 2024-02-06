package com.example.smartparking.models;

public class TimeNotification {

    private boolean limitTimeNotification;
    private boolean nearTimeNotification;
    private boolean emptySlot;
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

    public boolean isEmptySlot() {
        return emptySlot;
    }

    public void setEmptySlot(boolean emptySlot) {
        this.emptySlot = emptySlot;
    }
}
