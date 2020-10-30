package com.example.group_9_project.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.group_9_project.R;
import com.example.group_9_project.ui.model.Restaurant;
import com.example.group_9_project.ui.model.RestaurantManager;

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

        readRestaurantData();
    }




    //based on Brian Fraser's video
    private void readRestaurantData() {
        InputStream is =getResources().openRawResource(R.raw.restaurants_itr1);
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


                restaurants.addRestaurant(r);
                Log.d("MyActivity", "Just created: " + r);

            }

        }catch(IOException e){
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }

    private String removeQuotes(String s){
        String noQuotes = s.substring(1, s.length()-1);
        return noQuotes;
    }


}