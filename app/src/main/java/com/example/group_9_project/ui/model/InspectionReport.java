package com.example.group_9_project.ui.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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


    public String getInspectDateString() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String textDate = today.format(formatter);
        int currDate = Integer.parseInt(textDate);

        String inspectDateSt = "";
        //within 30 days
        if(inspectDate <= currDate + 30){
            int days = currDate - inspectDate;
            inspectDateSt = days + "days ago";
        }
        //less than a year ago

        return inspectDateSt;
    }

    private int dateToInt(LocalDate date){

    }
}
