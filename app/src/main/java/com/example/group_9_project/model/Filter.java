package com.example.group_9_project.model;

public class Filter {
    private RestaurantManager manager = RestaurantManager.getInstance();

    private String name;
    private InspectionReport.HazardRating hazard;
    private int criticalViolations;
    private boolean lessThanOrEqualTo; //Is true when critical inspections is <= and false when >=
    private boolean isFavourite;


    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InspectionReport.HazardRating getHazard() {
        return hazard;
    }

    public void setHazard(InspectionReport.HazardRating hazard) {
        this.hazard = hazard;
    }

    public int getCriticalViolations() {
        return criticalViolations;
    }

    public void setCriticalViolations(int criticalViolations) {
        this.criticalViolations = criticalViolations;
    }

    public boolean isLessThanOrEqualTo() {
        return lessThanOrEqualTo;
    }

    public void setLessThanOrEqualTo(boolean lessThanOrEqualTo) {
        this.lessThanOrEqualTo = lessThanOrEqualTo;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    //Other member functions

}
