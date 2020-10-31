package com.example.group_9_project.ui.model;

import java.util.ArrayList;

public class InspectionManager {
    private ArrayList<InspectionReport> manager;
    //private String trackingNum;

    public InspectionManager() {
        this.manager = new ArrayList<InspectionReport>();
        //this.trackingNum = t;
    }

    //getters

    //returns inspection report at element i
    public InspectionReport getInspection(int i){
        return manager.get(i);
    }

    public int getSize(){
        return manager.size();
    }

    //returns tracking number
//    public String getTrackingNum() {
//        return trackingNum;
//    }

    //setters

    public void addInspection(InspectionReport report){
        if(manager.size() == 0){
            manager.add(report);
        }
        else{
            int i = 0;
            int date = manager.get(i).getInspectDate();
            i++;
            while( i < manager.size()  && (report.getInspectDate() > date)){
                date = manager.get(i).getInspectDate();
                i++;
            }
            manager.add(i, report );
        }

    }
}
