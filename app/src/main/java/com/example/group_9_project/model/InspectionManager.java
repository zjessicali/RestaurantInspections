package com.example.group_9_project.model;

import android.content.SharedPreferences;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

//Stores inspection reports
public class
InspectionManager {
    private ArrayList<InspectionReport> manager;
    private int lastInspDate;


    public InspectionManager() {
        this.manager = new ArrayList<InspectionReport>();
        lastInspDate = 0;

    }

    //getters

    public int getListSize() {
        return manager.size();
    }

    public InspectionReport getLatest() {
        if (manager.size() == 0)
            return null;
        return manager.get(0);
    }

    //returns inspection report at element i
    public InspectionReport getInspection(int i){
        return manager.get(i);
    }

    public int getSize(){
        return manager.size();
    }

    public int getLastInspDate(){
        return lastInspDate;
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

        lastInspDate = manager.get(0).getInspectDate();
    }


}
