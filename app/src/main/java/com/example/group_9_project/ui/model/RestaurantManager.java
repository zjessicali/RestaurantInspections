package com.example.group_9_project.ui.model;

import java.util.ArrayList;
import java.util.Iterator;

public class RestaurantManager {
    ArrayList<Restaurant> manager;

    //constructor
    public RestaurantManager() {
        this.manager = new ArrayList<Restaurant>();
    }

    //access Restaurant at index i
    public Restaurant getRestFromIndex(int i){
        return manager.get(i);
    }

    //access Restaurant with tracking number t
    public Restaurant getRestFromTracking(String t){
        for(Restaurant r:manager){
            if(r.getTrackingNum().equals(t)){
                return r;
            }
        }
//        for(int i = 0; i < manager.size();i++){
//            if(manager.get(i).getTrackingNum() == t){
//                return manager.get(i);
//            }
//        }
        return null;
    }

    public int getSize(){
        return manager.size();
    }

    //needs testing
    //add restaurant r to the manager, maintain order
    public void addRestaurant(Restaurant r){
        if(manager.size() == 0){
            manager.add(r);
        }
        else{
            //manager.add(r);
            int i = 0;
            int i2 = 1;
            Restaurant curr = manager.get(i);
            //i++;
            while( i2 < manager.size() && (curr.getName()).compareTo(r.getName()) <= 0 ){
                curr = manager.get(i2);
                i++;
                i2++;
            }
            if(i2>manager.size()){
                manager.add(r);
            }
            else{
                manager.add(i, r);
            }
        }
    }







}
