package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private RestaurantManager restaurants = RestaurantManager.getInstance();; //feel free to rename
    int restaurant_num = 0, inspection_num = 0;
    TextView title;
    ListView RestaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


        title = findViewById(R.id.surrey_restaurant_list);
        RestaurantList = findViewById(R.id.restaurant_list);
        title.setText(getResources().getString(R.string.surrey_restaurant_list));


        readRestaurantData();
        readInspectionData();
        populateListView();

    }

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        RestaurantList.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter(){
            super(MainActivity.this, R.layout.listview_each_restaurant, restaurants.getListOfRestaurants());
        }
        @Override
        @NonNull
        public View getView(int pos, View convertView, @NonNull ViewGroup parent) {

            View restaurantView = convertView;

            if (restaurantView == null) {
                restaurantView = getLayoutInflater().inflate(R.layout.listview_each_restaurant, parent, false);
            }
            Restaurant currentRestaurant = restaurants.getRestFromIndex(pos);
            TextView restaurantnameField = restaurantView.findViewById(R.id.restaurant_label_name);
            restaurantnameField.setText(currentRestaurant.getName());
            restaurantnameField.setSelected(true);
            TextView restaurantCityField = restaurantView.findViewById(R.id.restaurant_city);
            restaurantCityField.setText(currentRestaurant.getCity());

            TextView restaurantAddressField = restaurantView.findViewById(R.id.restaurant_address);
            restaurantAddressField.setText(currentRestaurant.getAddress());

            InspectionManager inspections = currentRestaurant.getInspections();
            InspectionReport inspectionsForCurrentRestaurant = inspections.getInspection(inspections.getSize() - 1);
            int temp_inspection = 0;
            if (inspectionsForCurrentRestaurant != null) {
                temp_inspection = inspectionsForCurrentRestaurant.getNumCritical();
            }
            String display;

            TextView problemsFoundForCurrentRestaurantField = restaurantView.findViewById(R.id.restaurant_problemsFound);
            display = String.format("Issues Found", temp_inspection);
            problemsFoundForCurrentRestaurantField.setText(display);

            ImageView hazardLevelImage = restaurantView.findViewById(R.id.restaurant_image_hazardLevelValue);
            TextView leveltxt = restaurantView.findViewById(R.id.restaurant_hazardLevel);
            if (inspections.getSize() != 0) {

                InspectionReport.HazardRating latestInspectionHazardRating = inspectionsForCurrentRestaurant.getHazard();

                if (latestInspectionHazardRating == InspectionReport.HazardRating.HIGH) {
                    hazardLevelImage.setImageResource(R.drawable.high_risk);
                    display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
                            getResources().getString(R.string.restaurant_hazardLevelHigh_value));
                } else if (latestInspectionHazardRating == InspectionReport.HazardRating.MODERATE) {
                    hazardLevelImage.setImageResource(R.drawable.medium_risk);
                    restaurantView.setBackground(getDrawable(R.drawable.mid_back));
                    display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
                            getResources().getString(R.string.restaurant_hazardLevelModerate_value));
                } else {
                    hazardLevelImage.setImageResource(R.drawable.low_risk);
                    restaurantView.setBackground(getDrawable(R.drawable.low_back));
                    display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
                            getResources().getString(R.string.restaurant_hazardLevelLow_value));
                }
            }
         else{
                    hazardLevelImage.setImageResource(R.drawable.low_risk);
                    display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
                            getResources().getString(R.string.restaurant_hazardLevelNo_value));

                }

            leveltxt.setText(display);

            TextView latestInspectionTimeField =
                    restaurantView.findViewById(R.id.restaurant_label_latestInspection);
            if (inspections.getSize() != 0) {
                String inspectionDate = currentRestaurant.getLatest().getFormattedDate();
                display = String.format(
                        getResources().getString(R.string.restaurant_inspectionPerformedOn),
                        inspectionDate);
                latestInspectionTimeField.setText(display);
            } else
                display = String.format(
                        getResources().getString(R.string.restaurant_inspectionPerformedOn),
                        (getResources().getString(R.string.restaurant_noInspectionAvailable_value)));
            latestInspectionTimeField.setText(display);

            return restaurantView;

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