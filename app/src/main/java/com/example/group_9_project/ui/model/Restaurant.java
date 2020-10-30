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

    public String getTrackingNum() {
        return trackingNum;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    //setters

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
