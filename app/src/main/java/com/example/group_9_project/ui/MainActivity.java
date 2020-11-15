package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;
import com.example.group_9_project.network.FetchData;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {
    private RestaurantManager restaurants = RestaurantManager.getInstance();//feel free to rename
    private List<Restaurant>ResList = new ArrayList<Restaurant>(){};
    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREFS_LAST_UPDATE = "LastUpdatedPrefs";
    private static final String PREFS_RESTAURANTS = "RestaurantManagerPrefs";
    //private static final String TAG = "FetchingData";
    TextView title;
    ListView RestaurantList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.surrey_restaurant_list));
        //setRetainInstance(true);
        new FetchItemsTask().execute();
        //Log.d("FetchData parseItems", "Restaurant 2: " + restaurants.getRestFromIndex(1));
        populateRestaurants();

        needUpdate();
        populateListView();
        registerClickCallback();

    }

    private void populateRestaurants() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Gson lensGson = new Gson();
        String lensJson = prefs.getString(PREFS_RESTAURANTS,null );
        Type type = new TypeToken<List<Restaurant>>() {}.getType();
        List<Restaurant> storedRestaurants = lensGson.fromJson(lensJson, type);

        //if nothing in SharedPref, read raw
        if(storedRestaurants == null){
            readRawRestaurantData();
            readRawInspectionData();
        }
        //else populate the manager with SharedPref restaurants
        else{
            for(int i = 0; i < storedRestaurants.size(); i++){
                restaurants.addRestaurant(storedRestaurants.get(i));
            }
        }

        String last = prefs.getString(PREFS_LAST_UPDATE,"");
        restaurants.setLastModified(last);
    }

    private void storeRestaurantsToPref(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        List<Restaurant> storedRestaurants = new ArrayList<>();

        for(int i = 0; i < restaurants.getSize();i++){
            storedRestaurants.add(restaurants.getRestFromIndex(i));
        }

        String json = gson.toJson(storedRestaurants);
        editor.putString(PREFS_RESTAURANTS, json);
        editor.apply();
        putLastUpdateToSharedPref(restaurants.getLastModified());
    }

    //check if there is new data before u run this
    private boolean needUpdate() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //int defaultAmount = this.getResources().getInteger(R.integer.default_gems);
        //check if updated within 20 hours
        LocalDateTime now = LocalDateTime.now();
        String last = prefs.getString(PREFS_LAST_UPDATE,"");
        if(last.equals("")){
            return true;
        }
        LocalDateTime lastUpdated = LocalDateTime.parse(last);
        if(lastUpdated.isBefore(now.minusHours(20))){
            return true;
        }
        return false;
    }

    private String DateTimeToString(LocalDateTime dateTime){
        String lastUpdate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        lastUpdate = dateTime.format(formatter);
        return lastUpdate;
    }

    private void putLastUpdateToSharedPref(String lastUpdate){
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_LAST_UPDATE, lastUpdate);
        editor.apply();
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.restaurant_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = RestaurantDetail.launchIntent(MainActivity.this, position);
                startActivity(intent);
            }
        });
    }


    private void populateListView() {
        for (int i = 0; i < restaurants.getSize(); i++) {
            ResList.add(restaurants.getRestFromIndex(i));
        }
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurant_list);
        list.setAdapter(adapter);

    }

    private class MyListAdapter extends ArrayAdapter<Restaurant>{
        public MyListAdapter(){
            super(MainActivity.this, R.layout.listview_each_restaurant, ResList);
        }
        @Override
        public  View getView(int position, View convertView, ViewGroup parent){
            //Make sure we have a view to work with
            View itemView = convertView;
            if(itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.listview_each_restaurant, parent, false);
            }

            Restaurant currentRestaurant = ResList.get(position);
            InspectionManager inspections = currentRestaurant.getInspections();

            ImageView imageView = itemView.findViewById(R.id.restaurant_image_icon);
            imageView.setImageResource(R.drawable.restaurant_logo);

            ImageView hazardImage = itemView.findViewById(R.id.restaurant_image_hazardLevelValue);
            if(inspections.getSize() != 0){
                switch(inspections.getInspection(0).getHazard()){
                    case LOW:
                        hazardImage.setImageResource(R.drawable.low_risk);
                        break;
                    case MODERATE:
                        hazardImage.setImageResource(R.drawable.medium_risk);
                        break;
                    case HIGH:
                        hazardImage.setImageResource(R.drawable.high_risk);
                        break;
                }
            }


            TextView nameText = itemView.findViewById(R.id.restaurant_label_name);
            nameText.setText(currentRestaurant.getName());

            TextView addressText = itemView.findViewById(R.id.restaurant_address);
            addressText.setText(currentRestaurant.getAddress());

            TextView cityText = itemView.findViewById(R.id.restaurant_city);
            cityText.setText(currentRestaurant.getCity());

            TextView dateText = itemView.findViewById(R.id.restaurant_label_latestInspection);
            if(inspections.getSize()!=0){
                String date = "Last inspection: "+ inspections.getInspection(0).getInspectDateString();
                dateText.setText( date);
            }
            else{
                String date = "Last inspection: never";
                dateText.setText( date);
            }

            TextView issues = itemView.findViewById(R.id.restaurant_problemsFound);
            if(inspections.getSize()!= 0){
                int problems = inspections.getInspection(0).getNumCritical() + inspections.getInspection(0).getNumNonCritical();
                String issuesText = "Issues: "+ problems;
                issues.setText(issuesText);
            }
            else{
                String issuesText = "Issues: "+ 0;
                issues.setText(issuesText);
                issues.setText(issuesText);
            }

            return itemView;
        }

    }

    private void readRawInspectionData(){
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        restaurants.readInspectionData(reader);
    }



    private void readRawRestaurantData(){
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        restaurants.readRestaurantData(reader);
    }



    //Bill Phillips, Chris Stewart, Kristin Marsicano - Android Programming_ The Big Nerd Ranch Guide (2017, Big Nerd Ranch)
    private class FetchItemsTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new FetchData().fetchItems();
            restaurants = RestaurantManager.getInstance();
            Log.d("FetchData parseItems", "Restaurant 2: " + restaurants.getRestFromIndex(1));
            return null;
        }
    }

}