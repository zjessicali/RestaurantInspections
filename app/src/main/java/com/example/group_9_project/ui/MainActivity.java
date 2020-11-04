package com.example.group_9_project.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    private RestaurantManager restaurants;//feel free to rename

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurants = restaurants.getInstance();

        testRestaurantDetail(); //DELETE THIS
        readRestaurantData();
        readInspectionData();

    }



    private void readInspectionData() {
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

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

                if(tokens.length >=7 && tokens[6].length() > 0){
                    for(int i = 6; i < 9; i++){
                        inspection.addViolLump(removeQuotes(tokens[i]));
                    }
                }

//                Log.d("MyActivity", "Tracking number: " + trackingNum);
//                Log.d("MyActivity", "Restaurant: " + restaurants.getRestFromTracking(trackingNum));
                if(restaurants.getRestFromTracking(trackingNum) != null){
                    restaurants.getRestFromTracking(trackingNum).addInspection(inspection);
                    //Log.d("MyActivity", "Just created: " + inspection);
                }
            }

        }catch(IOException e){
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }

    //based on Brian Fraser's video

    private void readRestaurantData() {
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try{
            //headers
            reader.readLine();
            while( (line = reader.readLine()) != null){
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                Restaurant r = new Restaurant(removeQuotes(tokens[0]));
                r.setName(removeQuotes(tokens[1]));
                r.setAddress(removeQuotes(tokens[2]));
                r.setCity(removeQuotes(tokens[3]));
                r.setFacType(removeQuotes(tokens[4]));
                r.setLatitude(Double.parseDouble(tokens[5]));
                r.setLongitude(Double.parseDouble(tokens[6]));

                restaurants.addRestaurant(r);
                //Log.d("MyActivity", "Just created: " + r);
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
        return noQuotes;
    }

}