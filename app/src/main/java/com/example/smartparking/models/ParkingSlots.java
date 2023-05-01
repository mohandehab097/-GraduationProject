package com.example.smartparking.models;

import java.util.Map;

public class ParkingSlots {


    private String slotName;

    public ParkingSlots() {
    }

    public ParkingSlots(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }
}
