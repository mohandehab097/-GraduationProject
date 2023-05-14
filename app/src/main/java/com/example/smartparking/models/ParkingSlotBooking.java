package com.example.smartparking.models;

import java.util.Date;

public class ParkingSlotBooking {


    private String userId;
    private String userName;
    private Date bookingDate;
    private String strBookingDate;
    private String time;
    private String licenseNumber;
    private String liceseCharacter;
    private String licenseNumbersAndCharacter;
    private String sendNotification;
    private String Limittime;
    private boolean isArrived;



    public ParkingSlotBooking(){

    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public String getStrBookingDate() {
        return strBookingDate;
    }

    public void setStrBookingDate(String strBookingDate) {
        this.strBookingDate = strBookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLiceseCharacter() {
        return liceseCharacter;
    }

    public void setLiceseCharacter(String liceseCharacter) {
        this.liceseCharacter = liceseCharacter;
    }

    public String getLicenseNumbersAndCharacter() {
        return licenseNumbersAndCharacter;
    }

    public void setLicenseNumbersAndCharacter(String licenseNumbersAndCharacter) {
        this.licenseNumbersAndCharacter = licenseNumbersAndCharacter;
    }

    public String getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(String sendNotification) {
        this.sendNotification = sendNotification;
    }

    public String getLimittime() {
        return Limittime;
    }

    public void setLimittime(String limittime) {
        Limittime = limittime;
    }

    public boolean isArrived() {
        return isArrived;
    }

    public void setArrived(boolean arrived) {
        isArrived = arrived;
    }
}
