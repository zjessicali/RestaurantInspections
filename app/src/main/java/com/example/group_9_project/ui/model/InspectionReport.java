package com.example.group_9_project.ui.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

//Inspection Report with details about the inspection, as well as the violation
public class InspectionReport {
    public enum InspType {
        ROUTINE,
        FOLLOWUP

    }
    public enum HazardRating {
        LOW,
        MODERATE,
        HIGH
        }
    //private String trackingNum;
    private int inspectDate;
    private InspType type;
    private int numCritical;
    private int numNonCritical;
    private HazardRating hazard;
    private ArrayList<String> violLump;

    //constructor
    public InspectionReport() {
        violLump = new ArrayList<String>();
        int numCritical = 0;
        int numNonCritical = 0;
        int numTotal = 0;
    }

    //getters

    public int getInspectDate() {
        return inspectDate;
    }

    //gets inspect type
    public InspType getInspType() {
        return type;
    }

    //gets inspect type formatted as a string
    public String getInspTypeStr(){
        String st = "";
        switch(type){
            case ROUTINE:
                st = "Routine";
                break;
            case FOLLOWUP:
                st = "Follow-Up";
        }
        return st;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public HazardRating getHazard() {
        return hazard;
    }

    public ArrayList<String> getViolLump() {
        return violLump;
    }

    //needs testing
    //returns "when something happened in intelligent format" as a string
    public String getInspectDateString() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String textDate = today.format(formatter);
        int currDate = Integer.parseInt(textDate);

        String inspectDateSt = "";
        //within 30 days
        if (inspectDate >= currDate - 100) {
            int days = currDate - inspectDate;
            inspectDateSt = days + " days ago";
        }
        //less than a year ago
        else if (inspectDate > currDate - 10000) {
            //May 12
            int month = extractMonth();
            int date = extractDate();

            String st = Month.of(month).name();
            String monthSt = st.substring(0,1) + st.substring(1, st.length()).toLowerCase();

            //put into string
            inspectDateSt = monthSt + " " + date;
        } else {
            //May 2018
            int month = extractMonth();
            int year = extractYear();

            String st = Month.of(month).name();
            String monthSt = st.substring(0,1) + st.substring(1, st.length()).toLowerCase();

            inspectDateSt = monthSt + " " + year;
        }

        return inspectDateSt;
    }

    private int extractMonth() {
        int month = (inspectDate % 10000) / 100;
        return month;
    }

    private int extractDate() {
        int date = inspectDate % 100;
        return date;
    }

    private int extractYear() {
        int year = inspectDate / 10000;
        return year;
    }

    //gets full date, ex: May 12, 2018
    public String getFullDate(){
        String fullDate = "";
        int month = extractMonth();
        String st = Month.of(month).name();
        String monthSt = st.substring(0,1) + st.substring(1, st.length()).toLowerCase();

        fullDate += monthSt;

        int day = extractDate();
        fullDate += " " + day + ", ";

        int year = extractYear();
        fullDate += year;

        return fullDate;
    }

    @Override
    public String toString() {
        return "InspectionReport{" +
                "inspectDate=" + inspectDate +
                ", type=" + type +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazard=" + hazard +
                ", violLump=" + violLump +
                '}';
    }


    //setters

    public void setInspectDate(int inspectDate) {
        this.inspectDate = inspectDate;
    }

    //set inspect type directly
    public void setInspType(InspType type) {
        this.type = type;
    }

    //set inspect type with the string form ie. Follow-up instead of FOLLOWUP
    public void setInspType(String t) {
        switch(t){
            case "Routine":
                this.type = InspType.ROUTINE;
                break;
            case "Follow-Up":
                this.type = InspType.FOLLOWUP;
        }
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    //set hazard rating directly
    public void setHazard(HazardRating hazard) {
        this.hazard = hazard;
    }

    //set hazard rating with a string
    public void setHazard(String rating){
        switch(rating){
            case "Low":
                this.hazard = HazardRating.LOW;
                break;
            case "Moderate":
                this.hazard = HazardRating.MODERATE;
                break;
            case "High":
                this.hazard = HazardRating.HIGH;
                break;
        }
    }

    public void addViolLump(String s) {
        this.violLump.add(s);
    }
}
