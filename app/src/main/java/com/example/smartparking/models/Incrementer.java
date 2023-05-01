package com.example.smartparking.models;

public class Incrementer {


    private Integer incrementValue;

    public Incrementer() {
    }

    public Incrementer(Integer incrementValue) {
        this.incrementValue = incrementValue;
    }


    public Integer getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(Integer incrementValue) {
        this.incrementValue = incrementValue;
    }
}
