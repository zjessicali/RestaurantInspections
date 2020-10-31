package com.example.group_9_project.ui.model;

//Restaurant object with data about the restaurant as well as all the inspections
public class Restaurant {
    private String trackingNum;
    private String name;
    private String address;
    private String city;
    public enum FacType{RESTAURANT}
    private FacType type;
    private double latitude;
    private double longitude;
    private InspectionManager inspections;


    public Restaurant(String tracking) {
        this.trackingNum = tracking;
        this.type = FacType.RESTAURANT;
        this.inspections = new InspectionManager();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    //gets gps coordinates latitude x longitude
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

    public FacType getType() {
        return type;
    }

    //returns the inspection manager
    public InspectionManager getInspections() {
        return inspections;
    }

    //setters

//    public void setTrackingNum(String trackingNum) {
//        this.trackingNum = trackingNum;
//    }

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

    public void setFacType(String s){
        if(s == "Restaurant"){
            this.type = FacType.RESTAURANT;
        }
    }

    //add an inspection report to the manager
    public void addInspection(InspectionReport report){
        inspections.addInspection(report);
    }




    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNum='" + trackingNum + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", factype='" + type + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
