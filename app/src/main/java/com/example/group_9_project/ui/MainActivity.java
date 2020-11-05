package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RestaurantManager restaurants;//feel free to rename
    private List<Restaurant>ResList = new ArrayList<Restaurant>(){};
    TextView title;
    ListView RestaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurants = new RestaurantManager();
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.surrey_restaurant_list);
        RestaurantList = findViewById(R.id.restaurant_list);
        title.setText(getResources().getString(R.string.surrey_restaurant_list));
        readRestaurantData();
        readInspectionData();
        populateResList();
        populateListView();
        registerClickCallback();

    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.restaurant_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Restaurant clickedRes= ResList.get(position);
                String message = "You clicked  on the restaurant " + clickedRes.getName() + " at the position " + position;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateResList(){

        ResList.add(new Restaurant("104 Sushi & Co","10422 168 St","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("Lee Yuen Seafood Restaurant","1812 152 St","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("Lee Yuen Seafood Restaurant","14755 104 St","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("Pattullo A & W","12808 King George Blvd","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("The Unfindable Bar","12345 67 Ave","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("Top In Town Pizza","14330 64 Ave","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("Top In Town Pizza","12788 76A Ave","Surrey", R.drawable.restaurant_logo));
        ResList.add(new Restaurant("Zugba Flame Grilled Chicken","14351 104 Ave","Surrey", R.drawable.restaurant_logo));
    }

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        RestaurantList.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant>{
        public MyListAdapter(){
            super(MainActivity.this, R.layout.listview_each_restaurant,ResList);
        }
        @Override
        public  View getView(int position, View convertView, ViewGroup parent){
            //Make sure we have a view to work with
            View itemView = convertView;
            if(itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.listview_each_restaurant,parent,false);
            }

            Restaurant currentRestaurant = ResList.get(position);
            ImageView imageView = itemView.findViewById(R.id.restaurant_image_icon);
            imageView.setImageResource(currentRestaurant.getIconID());

            TextView nameText = itemView.findViewById(R.id.restaurant_label_name);
            nameText.setText(currentRestaurant.getName());

            TextView addressText = itemView.findViewById(R.id.restaurant_address);
            addressText.setText(currentRestaurant.getAddress());

            TextView cityText = itemView.findViewById(R.id.restaurant_city);
            cityText.setText(currentRestaurant.getCity());

            return itemView;
        }

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

                String lump = "";
                for(int i = 6; i < tokens.length; i++){
                    lump += removeQuotes(tokens[i]) + ",";
                }
                Log.d("MyActivity", "lump looks like: " + lump);

                inspection.processLump(lump);
                Log.d("MyActivity", "violLump size: " + inspection.getViolLump().size());
                for(int i = 0; i < inspection.getViolLump().size(); i++){
                    Log.d("MyActivity", "Violation: " + inspection.getViolation(i));
                }
                //Log.d("MyActivity", "Violation: " + inspection.getViolation(0));

                //adds inspection into it's restaurants inspection manager
                if(restaurants.getRestFromTracking(trackingNum) != null){
                    restaurants.getRestFromTracking(trackingNum).addInspection(inspection);
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