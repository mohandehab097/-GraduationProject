package com.example.smartparking.models;

public class Payment {

    private String licenseNumber;
    private Integer userStayedHour;
    private Integer userStayedMinutes;
    private Integer userAmountToPay;


    public Payment(){

    }


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getUserStayedHour() {
        return userStayedHour;
    }

    public void setUserStayedHour(Integer userStayedHour) {
        this.userStayedHour = userStayedHour;
    }

    public Integer getUserStayedMinutes() {
        return userStayedMinutes;
    }

    public void setUserStayedMinutes(Integer userStayedMinutes) {
        this.userStayedMinutes = userStayedMinutes;
    }

    public Integer getUserAmountToPay() {
        return userAmountToPay;
    }

    public void setUserAmountToPay(Integer userAmountToPay) {
        this.userAmountToPay = userAmountToPay;
    }
}
