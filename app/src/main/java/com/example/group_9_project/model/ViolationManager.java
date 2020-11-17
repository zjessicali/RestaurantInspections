package com.example.group_9_project.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//stores violations
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
    public int size(){
        return violationList.size();
    }

    @Override
    public Iterator<Violation> iterator() {
        return violationList.iterator();
    }
}
