package com.example.group_9_project.model;


import java.util.ArrayList;
import java.util.Iterator;

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

    public String violLumpToString(){
        String lump = "";
        String viol="";
        for(Violation v:violationList){
            for(String s:v.getViolation()){
                viol = s+",";
            }
            viol.substring(0, viol.length()-1);
            lump += viol + "|";
        }
        lump.substring(0,lump.length() -1);
        return lump;
    }

    @Override
    public Iterator<Violation> iterator() {
        return violationList.iterator();
    }
}
