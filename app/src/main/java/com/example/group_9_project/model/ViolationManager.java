package com.example.group_9_project.model;

import android.os.strictmode.Violation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViolationManager implements Iterable<Violation>{
    public List<Violation> violationList = new ArrayList<>();


    public void add( Violation violLumpString){
        //need to divide string into substrings, then convert them to values, then add as viol
        violationList.add(violLumpString);
    }

    @Override
    public Iterator<Violation> iterator() {
        return violationList.iterator();
    }
}
