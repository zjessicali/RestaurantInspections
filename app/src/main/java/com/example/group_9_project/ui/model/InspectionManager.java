package com.example.group_9_project.ui.model;

import java.util.ArrayList;

public class InspectionManager {
    private ArrayList<InspectionReport> manager;
    private String trackingNum;

    public InspectionManager() {
        this.manager = new ArrayList<InspectionReport>();
        //this.trackingNum = t;
    }

    //getters

    public InspectionReport getInspection(int i){
        return manager.get(i);
    }

    public String getTrackingNum() {
        return trackingNum;
    }
}
