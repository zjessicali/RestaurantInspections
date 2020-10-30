package com.example.group_9_project.ui.model;

import java.util.ArrayList;
import java.util.Iterator;

public class RestaurantManager {
    ArrayList<Restaurant> manager;

    //constructor
    public RestaurantManager(ArrayList<Restaurant> manager) {
        this.manager = new ArrayList<Restaurant>();
    }

    //access Restaurant at index i
    public Restaurant getRestFromIndex(int i){
        return manager.get(i);
    }

    //access Restaurant with tracking number t
    public Restaurant getRestFromTracking(String t){
        for(Restaurant r:manager){
            if(r.getTrackingNum() == t){
                return r;
            }
        }
        return null;
    }

    //needs testing
    //add restaurant r to the manager, maintain order
    public void addRestaurant(Restaurant r){
        if(manager.size() == 0){
            manager.add(r);
        }
        else{
            int i = 0;
            Restaurant curr = manager.get(i);
            while((curr.getName()).compareTo(r.getName()) <= 0 || i <manager.size()){
                i++;
                curr = manager.get(i);
            }
            manager.add(i, r);
        }
    }





}
