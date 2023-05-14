package com.example.smartparking.models;

public class PaymentNotification {


    private String licenseNumber;
    private String sendNotification;



    public PaymentNotification(){

    }


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(String sendNotification) {
        this.sendNotification = sendNotification;
    }
}
