package com.example.smartparking.models;

public class Payment {

    private String licenseNumber;
    private Integer stayedHourTime;
    private Integer stayedMinutesTime;
    private Integer theAmount;


    public Payment(){

    }


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getStayedHourTime() {
        return stayedHourTime;
    }

    public void setStayedHourTime(Integer stayedHourTime) {
        this.stayedHourTime = stayedHourTime;
    }

    public Integer getStayedMinutesTime() {
        return stayedMinutesTime;
    }

    public void setStayedMinutesTime(Integer stayedMinutesTime) {
        this.stayedMinutesTime = stayedMinutesTime;
    }

    public Integer getTheAmount() {
        return theAmount;
    }

    public void setTheAmount(Integer theAmount) {
        this.theAmount = theAmount;
    }
}
