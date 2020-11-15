package com.example.group_9_project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Stores restaurants in an object
public class RestaurantManager {
    private ArrayList<Restaurant> manager;
    private static RestaurantManager instance;
    private String lastModified;

    //constructor
    private RestaurantManager() {
        this.manager = new ArrayList<Restaurant>();
        lastModified = "";
    }
    //singleton
    public static RestaurantManager getInstance(){
        if(instance == null){
            instance = new RestaurantManager();
        }
        return instance;
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
        return null;
    }

    public int getSize(){
        return manager.size();
    }

    public String getLastModified(){
        return lastModified;
    }

    //add restaurant r to the manager, maintain order
    public void addRestaurant(Restaurant r){
        if(manager.size() == 0){
            manager.add(r);
        }
        else{
            int i = 0;
            int i2 = 1;
            Restaurant curr = manager.get(i);
            while( i2 < manager.size() && (curr.getName()).compareTo(r.getName()) <= 0 ){
                curr = manager.get(i2);
                i++;
                i2++;
            }
            if(i2>=manager.size()){
                //comp to last element
                if( (curr.getName()).compareTo(r.getName()) <= 0 ){
                    manager.add(r);
                }
                else{
                    manager.add(i, r);
                }
            }
            else{
                manager.add(i, r);
            }
        }
    }

    public void setLastModified(String s){
        lastModified = s;
    }
    public List<Restaurant> getListOfRestaurants() {
        return Collections.unmodifiableList(manager);
    }
}
