package com.example.group_9_project.model;

import java.util.ArrayList;

public class Violation {
    enum ViolType{
        FOOD,
        CHEMICAL,
        PEST,
        EQUIPMENT
    }
    private ArrayList<String> violation;
    private ViolType type;


    public Violation() {
        this.violation = new ArrayList<String>();
    }

    public String getFullViol(){
        String viol = "";
        return viol;
    }

    public ViolType getViolType(){
        return type;
    }


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
}
