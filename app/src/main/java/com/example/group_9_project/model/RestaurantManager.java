package com.example.group_9_project.model;

import android.util.Log;

import com.example.group_9_project.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.Charset;
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


    //based on Brian Fraser's video
    public void readRestaurantData(BufferedReader reader) {

        String line = "";
        try{
            //headers
            reader.readLine();
            while( (line = reader.readLine()) != null){
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                Restaurant r = new Restaurant(removeQuotes(tokens[0]));
                //if name has a comma
                int t = 1;
                if( tokens.length == 7){//normal
                    r.setName(removeQuotes(tokens[1]));
                    t = 2;
                    r.setAddress(removeQuotes(tokens[2]));
                    r.setCity(removeQuotes(tokens[3]));
                    r.setFacType(removeQuotes(tokens[4]));
                    r.setLatitude(Double.parseDouble(tokens[5]));
                    Log.d("MyActivity", "ID: " + tokens[0]);
                    Log.d("MyActivity", "Length should be 7: " + tokens.length);
                    r.setLongitude(Double.parseDouble(tokens[6]));
                }
                else{
                    String name = removeQuotes(tokens[1]) + ", " + removeQuotes(tokens[2]);
                    r.setName(name);
                    t = 3;
                    r.setAddress(removeQuotes(tokens[3]));
                    r.setCity(removeQuotes(tokens[4]));
                    r.setFacType(removeQuotes(tokens[5]));
                    r.setLatitude(Double.parseDouble(tokens[6]));
                    Log.d("MyActivity", "ID: " + tokens[0]);
                    Log.d("MyActivity", "Length should be 8: " + tokens.length);
                    r.setLongitude(Double.parseDouble(tokens[7]));
                }

                this.addRestaurant(r);
            }

        }catch(IOException e){
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }

    public void readInspectionData(BufferedReader reader) {
        String line = "";
        try{
            //headers
            reader.readLine();

            while( (line = reader.readLine()) != null){
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                InspectionReport inspection = new InspectionReport();
                String trackingNum = removeQuotes(tokens[0]);
                inspection.setInspectDate(Integer.parseInt(tokens[1]));
                inspection.setInspType(removeQuotes(tokens[2]));
                inspection.setNumCritical(Integer.parseInt(tokens[3]));
                inspection.setNumNonCritical((Integer.parseInt(tokens[4])));
                inspection.setHazard(removeQuotes(tokens[5]));

                String lump = "";
                for(int i = 6; i < tokens.length; i++){
                    lump += removeQuotes(tokens[i]) + ",";
                }

                inspection.processLump(lump);

                //adds inspection into it's restaurants inspection manager
                if(this.getRestFromTracking(trackingNum) != null){
                    this.getRestFromTracking(trackingNum).addInspection(inspection);
                }
            }

        }catch(IOException e){
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }

    public void readUpdatedInspectionData(BufferedReader reader) {
        String line = "";
        try{
            //headers
            reader.readLine();

            while( (line = reader.readLine()) != null){
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                InspectionReport inspection = new InspectionReport();
                String trackingNum = removeQuotes(tokens[0]);
                inspection.setInspectDate(Integer.parseInt(tokens[1]));
                inspection.setInspType(removeQuotes(tokens[2]));
                inspection.setNumCritical(Integer.parseInt(tokens[3]));
                inspection.setNumNonCritical((Integer.parseInt(tokens[4])));
                inspection.setHazard(removeQuotes(tokens[tokens.length - 1]));

                String lump = "";
                for(int i = 5; i < tokens.length -1; i++){
                    lump += removeQuotes(tokens[i]) + ",";
                }

                inspection.processLump(lump);

                //adds inspection into it's restaurants inspection manager
                if(this.getRestFromTracking(trackingNum) != null){
                    this.getRestFromTracking(trackingNum).addInspection(inspection);
                }
            }

        }catch(IOException e){
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }
    //Used https://stackoverflow.com/questions/2608665/how-can-i-trim-beginning-and-ending-double-quotes-from-a-string/34406001#:~:text=To%20remove%20one%20or%20more,%2B%24%22%2C%20%22%22)%3B
    private String removeQuotes(String s){
        //check if its "" first
        String noQuotes = "";
        if(s.startsWith("\"") && s.endsWith("\"")){
            noQuotes = s.substring(1, s.length()-1);
        }
        else if(s.startsWith("\"")){
            noQuotes = s.substring(1, s.length());
        }
        else if(s.endsWith("\"")){
            noQuotes = s.substring(0, s.length()-1);
        }
        else{
            return s;
        }
        return noQuotes;
    }
}
