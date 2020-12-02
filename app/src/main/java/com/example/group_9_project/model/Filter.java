package com.example.group_9_project.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Filter {
    private RestaurantManager manager = RestaurantManager.getInstance();

    private InspectionReport.HazardRating hazard;
    private int criticalViolations;
    private boolean lessThanOrEqualTo; //Is true when critical inspections is <= and false when >=
    private boolean isFavourite;


    //Getters and Setters

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
    public ArrayList<Restaurant> filterHazard(ArrayList<Restaurant> filter) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        for(Restaurant restaurant : filter) {
            InspectionManager inspectionManager = restaurant.getInspections();
            InspectionReport inspectionReport = inspectionManager.getInspection(0);
            if (inspectionReport.getHazard() == this.hazard) {
                restaurants.add(restaurant);
            }
        }

        return restaurants;
    }

    public ArrayList<Restaurant>  filterViolations(ArrayList<Restaurant> filter) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String textDate = today.format(formatter);
        int currDate = Integer.parseInt(textDate);

        ArrayList<Restaurant> restaurants = new ArrayList<>();

        for(Restaurant restaurant : filter) {
            int numCriticalViol = 0;
            InspectionManager inspectionManager = restaurant.getInspections();
            for (int i = 0; i < inspectionManager.getSize(); i++) {
                InspectionReport report = inspectionManager.getInspection(i);
                int inspectDate = report.getInspectDate();
                if (inspectDate > currDate - 10000) {
                    restaurants.add(restaurant);
                }
            }
        }

        return restaurants;
    }

    //DO THIS LATER
//
//    public ArrayList<Restaurant> filterFavourites(ArrayList<Restaurant> filter) {
//
//    }

}
