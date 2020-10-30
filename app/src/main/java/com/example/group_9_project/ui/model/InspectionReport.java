package com.example.group_9_project.ui.model;

import java.util.ArrayList;

public class InspectionReport {
    String trackingNum;
    int inspectDate;
    enum InspType {
        ROUTINE,
        FOLLOWUP
    }
    int numCritical;
    int numNonCritical;
    int numTotal;
    enum HazardRating{
        LOW,
        MEDIUM,
        HIGH
    }
    ArrayList<String> violLump;

    public InspectionReport() {

    }


    public int getInspectDate() {
        //if
        return inspectDate;
    }
}
