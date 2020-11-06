package com.example.group_9_project.model;

import java.util.ArrayList;

//Stores inspection reports
public class
InspectionManager {
    private ArrayList<InspectionReport> manager;


    public InspectionManager() {
        this.manager = new ArrayList<InspectionReport>();

    }

    //getters

    //returns inspection report at element i
    public InspectionReport getInspection(int i){
        return manager.get(i);
    }

    public int getSize(){
        return manager.size();
    }

    //setters

    public void addInspection(InspectionReport report){
        if(manager.size() == 0){
            manager.add(report);
        }
        else{
            int i = 0;
            int i2 = 1;
            int date = manager.get(i).getInspectDate();
            while( i2 < manager.size()  && (report.getInspectDate() < date)){
                date = manager.get(i2).getInspectDate();
                i++;
                i2++;
            }
            if(i2>= manager.size() && (report.getInspectDate() < date)){
                manager.add(report);
            }
            else{
                manager.add(i, report);
            }
        }


    }


}
