package com.example.group_9_project.ui.model;

public class Restaurant {
    String trackingNum;
    String name;
    String address;
    String city;
    enum FacType{RESTAURANT}
    double latitude;
    double longitude;
    //inspection reports?


    public Restaurant() {

    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCoords() {
        String coords = "" + latitude + " x " + longitude;
        return coords;
    }
}
