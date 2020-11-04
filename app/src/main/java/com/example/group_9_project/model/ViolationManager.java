package com.example.group_9_project.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViolationManager implements Iterable<Violation>{
    private ArrayList<Violation> violationList;

    public ViolationManager() {
        this.violationList = new ArrayList<Violation>();
    }

    public ArrayList<Violation> getViolLump() {
        return violationList;
    }

    //get violation at index i
    public Violation getViolation(int i){
        return violationList.get(i);
    }

    public void addViolation(Violation v){
        violationList.add(v);
    }

    @Override
    public Iterator<Violation> iterator() {
        return violationList.iterator();
    }
}
