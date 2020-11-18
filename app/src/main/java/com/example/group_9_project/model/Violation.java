package com.example.group_9_project.model;

import java.util.ArrayList;

//Violation object, stores one violation and some info
public class Violation {
    enum ViolType{
        FOOD,
        CHEMICAL,
        PEST,
        EQUIPMENT,
        LOCATION,
        CONTAINERS,
        HYGIENE,
        REQUIREMENTS
    }
    private ArrayList<String> violation;
    private ViolType type;


    public Violation() {
        this.violation = new ArrayList<String>();
    }

    //get full description of violation
    public String getFullViol(){
        String viol = "";
        return viol;
    }

    //get type as a ViolType
    public ViolType getViolType(){
        return type;
    }

    //get type as a string
    public String getViolTypeString(){
        switch(type){
            case FOOD:
                return "Food";
            case PEST:
                return "Pests";
            case HYGIENE:
                return "Hygiene";
            case CHEMICAL:
                return "Chemical";
            case LOCATION:
                return "Location";
            case EQUIPMENT:
                return "Equipment";
            case CONTAINERS:
                return "Containers";
            case REQUIREMENTS:
                return "Requirements";
        }
        return "No type";
    }

    //get violation array
    public ArrayList<String> getViolation() {
        return violation;
    }

    //get string saying critical or not critical
    public String getCritical(){
        return violation.get(1);
    }

    //get string saying repeat or not repeat
    public String getRepeat(){
        return violation.get(3);
    }

    //setter

    //for initial reading
    public void addToViol(String s) {
        this.violation.add(s);
    }

    @Override
    public String toString() {
        return "Violation{" +
                "violation=" + violation +
                ", type=" + type +
                '}';
    }

    public void setType(int id){
        if(id > 100 && id <= 104){
            type = ViolType.LOCATION;
        }
        else if(id > 200 && id <= 212){
            type = ViolType.FOOD;
        }
        else if(id > 300 && id <= 303 ){
            type = ViolType.EQUIPMENT;
        }
        else if(id > 303 && id <= 305){
            type = ViolType.PEST;
        }
        else if(id > 305 && id <= 309 ){
            type = ViolType.CHEMICAL;
        }
        else if(id > 309 && id <= 315 ){
            type = ViolType.CONTAINERS;
        }
        else if(id > 400 && id <= 404 ){
            type = ViolType.HYGIENE;
        }
        else if(id > 500 && id <= 502 ){
            type = ViolType.REQUIREMENTS;
        }

    }
}
