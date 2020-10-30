package com.example.group_9_project.ui.model;

import java.util.ArrayList;

public class InspectionManager {
    private ArrayList<InspectionReport> manager;

    public InspectionManager(ArrayList<InspectionReport> manager) {
        this.manager = new ArrayList<InspectionReport>();
    }

    public InspectionReport getInspection(int i){
        return manager.get(i);
    }

}
