package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;
import com.example.group_9_project.model.UpdateData;
import com.example.group_9_project.network.FetchData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AskUpdateFragment.AskUpdateListener, LoadingFragment.LoadingFragmentListener {
    private static final String TAG = "MapsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;

    private Boolean mLocationPermissionGranted = false;
    private RestaurantManager manager = RestaurantManager.getInstance();

    private ArrayList<Marker> markers = new ArrayList<>();
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREFS_LAST_UPDATE = "LastUpdatedPrefs";
    private UpdateData updateData = UpdateData.getInstance();
    private FetchItemsTask asyncTask = null;

    private boolean isOpened;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        isOpened = getIntent().getBooleanExtra("isOpened",false);
        if(!isOpened){
            setUpManager();
        }
        getLocationPermission();
    }

    //Source: https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    private void getLocationPermission() {
        Log.i(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.i(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    Log.i(TAG, "onRequestPermissionsResult: permission granted");
                    initMap();
                }
            }
        }
    }

    //Source: https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    private void initMap() {
        Log.i(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    //Source: https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=5
    private void getDeviceLocation() {
        Log.i(TAG, "getDeviceLocation: getting device location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation == null){
                                Log.d("MapsActivity", "currLocation null");
                            }else {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            }
                        } else {
                            Log.i(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.i(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    //Source: https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=5
    private void moveCamera(LatLng latLng) {
        Log.i(TAG, "moveCamera: moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    private void showPopUp(int index) {

        Restaurant restaurant = manager.getRestFromIndex(index);

        AlertDialog.Builder dialogBuider;
        AlertDialog dialog;

        dialogBuider = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.maps_dialog, null);

        TextView nameText = contactPopupView.findViewById(R.id.restaurantNameText);
        TextView addressText = contactPopupView.findViewById(R.id.addressText);
        TextView hazardText = contactPopupView.findViewById(R.id.hazardText);

        nameText.setText(getResources().getString(R.string.restaurantName) + restaurant.getName());
        addressText.setText(getResources().getString(R.string.restaurantAddress) + restaurant.getAddress());

        InspectionManager inspectionManager = restaurant.getInspections();
        String hazard = "";

        if (inspectionManager.getSize() > 0) {
            InspectionReport inspectionReport = inspectionManager.getInspection(0);

            switch (inspectionReport.getHazard()) {
                case LOW:
                    hazard = "low";
                    break;

                case MODERATE:
                    hazard = "moderate";
                    break;

                case HIGH:
                    hazard = "high";
                    break;
            }

        } else {hazard = "unknown";}

        hazardText.setText(getResources().getString(R.string.restaurantHazard) + hazard);


        dialogBuider.setView(contactPopupView);
        dialog = dialogBuider.create();
        dialog.show();

        final int finalIndex = index;
        ConstraintLayout popup = contactPopupView.findViewById(R.id.dialogConstraintLayout);
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantDetail.launchIntent(MapsActivity.this, finalIndex);
                startActivity(intent);
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Allow user to move around and zoom

        // Cluster pegs intelligently

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT)
                .show();
        Log.i(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        // Center on device's current
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        }


        // Display pegs on all restaurant's location
        for (int i = 0; i < manager.getSize(); i++) {
            Restaurant restaurant = manager.getRestFromIndex(i);
            LatLng restaurantLocation = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            final MarkerOptions marker = new MarkerOptions();
            marker.position(restaurantLocation);
            marker.title(restaurant.getName());

            InspectionManager inspectionManager = restaurant.getInspections();

            if (inspectionManager.getSize() != 0) {
                String hazard = "";


                InspectionReport latestInspection = inspectionManager.getInspection(0);

                switch (latestInspection.getHazard()) {
                    case HIGH:
                        hazard = "high";
                        break;

                    case MODERATE:
                        hazard = "moderate";
                        break;

                    case LOW:
                        hazard = "low";
                        break;

                    default:
                        hazard = "unknown";
                }


                marker.snippet("Hazard: " + hazard);
            } else { marker.snippet("Hazard: unknown");}

            Marker newMarker = mMap.addMarker(marker);
            newMarker.setTag(i);


            if (inspectionManager.getSize() != 0) {
                InspectionReport latestInspection = inspectionManager.getInspection(0);

                switch (latestInspection.getHazard()) {
                    case HIGH:
                        newMarker.setIcon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        break;

                    case MODERATE:
                        newMarker.setIcon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        break;

                    case LOW:
                        newMarker.setIcon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        break;
                }
            }
            else {
                newMarker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));}

            markers.add(newMarker);
        }

        // Interact with peg to show more information
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i = 0; i < markers.size(); i++) {
                    if (marker.equals(markers.get(i))) {
                            int index = (int) marker.getTag();
                            showPopUp(index);
                    }
                }
                return false;
            }
        });


        // Toggle between map screen and restaurant screen
        Button button = findViewById(R.id.listViewBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static Intent launchIntent(Context context, boolean isOpened){
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("isOpened", isOpened);
        return intent;
    }

    private void setUpManager() {
        getLastUpdatedFromSharedPref();
        Log.d("MapsActivity", "update not null");
        if(updateData.getNeedUpdate() == null){//first time running, fill with itr1
            readRawRestaurantData();
            readRawInspectionData();

            //first time running means last update more than 20 hours -> ask if they want to update
            Log.d("MapsActivity", "it should ask update");
            askUpdate();
        }
        else if(updateData.getNeedUpdate()){//check if need update
            //populateListView();
            askUpdate();
        }
        else{
            //do nothing if don't need update
            MainActivity.getInstance().populateListView();
        }
    }

    private void askUpdate() {
        FragmentManager manager = getSupportFragmentManager();
        AskUpdateFragment updateDialog = new AskUpdateFragment();
        updateDialog.show(manager,"UpdateDialog");

        Log.i("MyActivity", "Showed dialog");
    }

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

    private void readRawInspectionData(){
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        manager.readInspectionData(reader);
    }

    private void readRawRestaurantData(){
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        manager.readRestaurantData(reader);
    }




    @Override
    public void startUpdate() {
        asyncTask = new FetchItemsTask();
        asyncTask.execute();
    }

    @Override
    public void updateNextTime() {
        updateData.setNeedUpdate(true);
        MainActivity.getInstance().populateListView();
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
        Log.d(TAG,"cancel clicked");

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
            loadingDialog.dismiss();
            RestaurantManager.completeDownload();
            MainActivity.getInstance().populateListView();
            //populate map??
            updateData.setNeedUpdate(false);
            LocalDateTime now = LocalDateTime.now();
            updateData.setLastUpdated(DateTimeToString(now));//double check this
            //implment cancel
        }
        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            asyncTask.cancel(true);
            Log.d(TAG,"cancel called");
            MainActivity.getInstance().populateListView();
            updateData.setNeedUpdate(true);
        }
    }
    private LoadingFragment loadingDialog = new LoadingFragment();

    private void openPleaseWaitDialog() {
        FragmentManager manager = getSupportFragmentManager();
        //LoadingFragment dialog = new LoadingFragment();
        loadingDialog.show(manager,"UpdateDialog");

        Log.i("MyActivity", "Showed loading dialog");

    }

    private String DateTimeToString(LocalDateTime dateTime){
        String lastUpdate;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        lastUpdate = dateTime.format(formatter);
        return lastUpdate;
    }
}