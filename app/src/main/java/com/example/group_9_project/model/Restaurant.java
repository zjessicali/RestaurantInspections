package com.example.group_9_project.model;

//Restaurant object with data about the restaurant as well as all the inspections
public class
Restaurant {
    private String trackingNum;
    private String name;
    private String address;
    private String city;
    public enum FacType{RESTAURANT}
    private FacType type;
    private double latitude;
    private double longitude;
    private int iconID;
    private String res_id;
    public InspectionManager inspections;


    public Restaurant(String tracking) {
        this.trackingNum = tracking;
        this.type = FacType.RESTAURANT;
        this.inspections = new InspectionManager();
    }

    public Restaurant(String name, String address, String city, int iconID) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.iconID=iconID;
    }

    public int getIconID(){
        return iconID;
    }
    public String getRes_id() { return res_id; }
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


    public void setName(String name) {
        this.name = name;
        IconsLib icon_lib = IconsLib.getInstance();
        String[] res_ids = icon_lib.getLibrary();
        String modifiedName = name.replaceAll("'|-|,|\\s", "")
                .replace("&", "and");

        for (String r: res_ids){
            if (modifiedName.contains(r)){
                res_id = r.toLowerCase().replaceAll("[0-9]","");
                return;
            }
        }

        res_id = "generic";
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

    public String getGPS(){
        String lat_s = String.format("%.4f", latitude);
        String lon_s = String.format("%.4f", longitude);
        String GPS = "(" + lat_s + ", " + lon_s + ")";
        return GPS;
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
