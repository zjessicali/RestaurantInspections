package com.example.group_9_project.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Filter {
    private RestaurantManager manager = RestaurantManager.getInstance();

    private InspectionReport.HazardRating hazard;
    private int criticalViolations;
    private boolean greaterThanOrEqualTo; //Is true when critical inspections is >= and false when <=


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

    public boolean isGreaterThanOrEqualTo() {
        return greaterThanOrEqualTo;
    }

    public void setGreaterThanOrEqualTo(boolean greaterThanOrEqualTo) {
        this.greaterThanOrEqualTo = greaterThanOrEqualTo;
    }

    //Other member functions
    public ArrayList<Restaurant> filterHazard(ArrayList<Restaurant> filter) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        for(Restaurant restaurant : filter) {
            if (restaurant.getInspections().getSize() > 0) {
                InspectionManager inspectionManager = restaurant.getInspections();
                InspectionReport inspectionReport = inspectionManager.getInspection(0);
                if (inspectionReport.getHazard() == this.hazard) {
                    restaurants.add(restaurant);
                }
            }
        }

        return restaurants;
    }

    public ArrayList<Restaurant>  filterViolations(ArrayList<Restaurant> filter) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String textDate = today.format(formatter);
        int currDate = Integer.parseInt(textDate);
        LocalDate yearAgo = intToDate(currDate-10000);

        ArrayList<Restaurant> restaurants = new ArrayList<>();


        for(Restaurant restaurant : filter) {
            int numCriticalViol = 0;
            InspectionManager inspectionManager = restaurant.getInspections();
            for (int i = 0; i < inspectionManager.getSize(); i++) {
                InspectionReport report = inspectionManager.getInspection(i);
                int inspectDate = report.getInspectDate();
                LocalDate inspectDateTime = intToDate(inspectDate);
                if (inspectDateTime.isAfter(yearAgo)) {
                    numCriticalViol = numCriticalViol +  report.getNumCritical();
                }
            }

            if(this.greaterThanOrEqualTo) {
                if(numCriticalViol >= this.criticalViolations) {
                    restaurants.add(restaurant);
                }
            }
            else {
                if (numCriticalViol <= this.criticalViolations) {
                    restaurants.add(restaurant);
                }
            }
        }

        return restaurants;
    }

    private LocalDate intToDate(int d){
        DateTimeFormatter f = DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ;
        //String tmp ="";
        int month = (d % 10000) / 100;
        int date = d % 100;
        int year = d / 10000;
//        if(month <10){
//            tmp = "0";
//        }

        LocalDate dateTime = LocalDate.of(year, month, date);
        return dateTime;

    }

    public ArrayList<Restaurant> filterFavourites(ArrayList<Restaurant> filter) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        for(Restaurant restaurant : filter) {
            if (restaurant.isFav()) {
                restaurants.add(restaurant);
            }
        }

        return restaurants;
    }

}
