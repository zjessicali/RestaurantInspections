package com.example.group_9_project.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Text;
import com.example.group_9_project.model.UpdateData;
import com.example.group_9_project.network.FetchData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements AskUpdateFragment.AskUpdateListener, LoadingFragment.LoadingFragmentListener {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private RestaurantManager restaurants = RestaurantManager.getInstance();//feel free to rename
    private List<Restaurant>ResList = new ArrayList<Restaurant>(){};
    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREFS_LAST_UPDATE = "LastUpdatedPrefs";
    private UpdateData updateData = UpdateData.getInstance();
    private FetchItemsTask asyncTask = null;
    public static final String TAG = "MyTag";
    AlertDialog alert_pleaseWait;
    RequestQueue RQueue;

    //NOTE TO JESSICA: DONT FORGET TO SETSHAREDPREF
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.surrey_restaurant_list));
        setUpManager();


        createMapIntent();
        setUpMapViewButton();
        readRawRestaurantData();
        readRawInspectionData();
        populateListView();
        //new FetchItemsTask().execute();
        //populateRestaurants();

        registerClickCallback();

    }

    private void createMapIntent() {
        if (isServicesOK()) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    }

    private void setUpMapViewButton() {
        Button button = findViewById(R.id.mapViewBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMapIntent();
            }
        });
    }

    //Permissions for google map
    //Source: https://www.youtube.com/watch?v=M0bYvXlhgSI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=3
    public boolean isServicesOK() {
        Log.i(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.i(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if( GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.i(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT)
                    .show();
        }

        return false;
    }


    private void setUpManager() {
        getLastUpdatedFromSharedPref();
        if(updateData.getNeedUpdate() == null){//first time running, fill with itr1
            readRawRestaurantData();
            readRawInspectionData();

            populateListView();
            //first time running means last update more than 20 hours -> ask if they want to update
            askUpdate();
        }
        else{//check if need update
            populateListView();
            new FetchLastModified().execute();
        }
    }

    private void askUpdate() {
        FragmentManager manager = getSupportFragmentManager();
        AskUpdateFragment dialog = new AskUpdateFragment();
        dialog.show(manager,"UpdateDialog");

        Log.i("MyActivity", "Showed dialog");
    }



    //maybe discard
//    private void populateRestaurants() {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//
//        Gson lensGson = new Gson();
//        String lensJson = prefs.getString(PREFS_RESTAURANTS,null );
//        Type type = new TypeToken<List<Restaurant>>() {}.getType();
//        List<Restaurant> storedRestaurants = lensGson.fromJson(lensJson, type);
//
//        //if nothing in SharedPref, read raw
//        if(storedRestaurants == null){
//            readRawRestaurantData();
//            readRawInspectionData();
//        }
//        //else populate the manager with SharedPref restaurants
//        else{
//            for(int i = 0; i < storedRestaurants.size(); i++){
//                restaurants.addRestaurant(storedRestaurants.get(i));
//            }
//        }
//
//        String last = prefs.getString(PREFS_LAST_UPDATE,"");
//        restaurants.setLastModified(last);
//    }

    //check if it's been 20 hours since you last updated
    private boolean needUpdate() {
        //check if updated within 20 hours
        LocalDateTime now = LocalDateTime.now();
        //String last = prefs.getString(PREFS_LAST_UPDATE,"");
        String last = updateData.getLastUpdated();
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
        String lastUpdate;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        lastUpdate = dateTime.format(formatter);
        return lastUpdate;
    }

    private void getLastUpdatedFromSharedPref(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Gson lensGson = new Gson();
        String lensJson = prefs.getString(PREFS_LAST_UPDATE,null );
        Type type = new TypeToken<List<UpdateData>>() {}.getType();
        List<UpdateData> storedData = lensGson.fromJson(lensJson, type);

        if(storedData != null) {
            updateData = storedData.get(0);
        }
    }

    //fix later
    private void putLastUpdateToSharedPref(String lastUpdate){
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        List<UpdateData> storedData = new ArrayList<>();

        storedData.add(updateData);

        String json = gson.toJson(storedData);
        editor.putString(PREFS_LAST_UPDATE, json);
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
        Log.d("MyActivity", "populate List size: "+ResList.size());
        //clear what was before first
        int resListSize = ResList.size();
        for(int i = 0; i < resListSize; i++){
            ResList.remove(0);
            Log.d("MyActivity", "ResList index: " + i);
        }
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
          //  **************************
            ImageView restaurantLogo = itemView.findViewById(R.id.restaurant_image_icon);
            String resourceId = currentRestaurant.getRes_id();

            int resId = MainActivity.this.getResources().getIdentifier(
                    resourceId,
                    "drawable",
                    MainActivity.this.getPackageName()
            );
            if (resId == 0) {
                restaurantLogo.setImageResource(R.drawable.restaurant_logo);
            } else {
                restaurantLogo.setImageResource(resId);
            }

            //    imageView.setImageResource(R.drawable.restaurant_logo);

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

    @Override
    public void startUpdate() {
        asyncTask = new FetchItemsTask();
        asyncTask.execute();
    }

    @Override
    public void updateNextTime() {
        updateData.setNeedUpdate(true);
    }

    private class FetchLastModified extends AsyncTask<Void,Void, UpdateData> {
        @Override
        protected UpdateData doInBackground(Void... params) {
            return new FetchData().fetchUpdateItems();
        }
        @Override
        protected void onPostExecute(UpdateData needUpdate) {
            updateData = needUpdate;
            //check if need update
            updateData.setNeedUpdate(needUpdate());
            if(updateData.getNeedUpdate()){
                //check if want update
                askUpdate();
            }
        }
    }

    @Override
    public void onCancelClicked() {
        //super.onStop();
        asyncTask.cancel(true);
        if (RQueue != null) {
            RQueue.cancelAll(TAG);
        }
    }
    //Bill Phillips, Chris Stewart, Kristin Marsicano - Android Programming_ The Big Nerd Ranch Guide (2017, Big Nerd Ranch)
    private class FetchItemsTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected void onPreExecute(){
            //https://www.tutorialspoint.com/how-to-cancel-an-executing-asynctask-in-android
            //open loading screen
            super.onPreExecute();
            openPleaseWaitDialog();

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            if (!isCancelled()) {
                new FetchData().fetchItems();
                return true;
            } else {
                return false;
            }
            //return new FetchData().fetchItems();
        }
        @Override
        protected void onPostExecute(Boolean done) {
            dialog.dismiss();
            //restaurants = manager;
            populateListView();
            updateData.setNeedUpdate(false);
            LocalDateTime now = LocalDateTime.now();
            updateData.setLastUpdated(DateTimeToString(now));//double check this
            //implment cancel
        }

    }
    private LoadingFragment dialog = new LoadingFragment();

    private void openPleaseWaitDialog() {
        FragmentManager manager = getSupportFragmentManager();
        //LoadingFragment dialog = new LoadingFragment();
        dialog.show(manager,"UpdateDialog");

        Log.i("MyActivity", "Showed loading dialog");

    }

}