package com.example.group_9_project.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.Filter;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    ListView list;
    private RestaurantManager restaurants = RestaurantManager.getInstance();//feel free to rename
    private static List<Restaurant>ResList = new ArrayList<Restaurant>(){};
    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREFS_LAST_UPDATE = "LastUpdatedPrefs";
     private static String search_name;
    private static final String PREFS_FAVORITES = "FavoritesPrefs";
    private UpdateData updateData = UpdateData.getInstance();
    private boolean[] isClicked = {false, false};
    private boolean[] selected = {false, false, false};//hazard,viol,fav
    ArrayList<Restaurant> filter = new ArrayList<>();


    private boolean mapIsOpened = false;
    private int populated = 0;
    private static MainActivity instance;

    private Filter filterer = new Filter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.surrey_restaurant_list));

        instance = this;

        createMapIntent(false);
        setUpMapViewButton();

        setupHazardButton();
        setupViolationsButton();
        setupResetButton();
        setupFavouritesButton();

        search();
        registerClickCallback();
    }

    private void clicked(int index) {
        isClicked[index] = true;
    }

    private void unclicked(int index) {
        isClicked[index] = false;
    }

    private boolean isClicked(int index) {
        return isClicked[index];
    }

    private void populateFilter() {
        filter.clear();
        for(int i = 0; i < restaurants.getSize(); i++) {
            filter.add(restaurants.getRestFromIndex(i));
        }
    }

    private void populateList() {
        Log.d("MyActivity", "populate List size: "+ResList.size());
        //clear what was before first
        int resListSize = ResList.size();
        for(int i = 0; i < resListSize; i++){
            ResList.remove(0);
            Log.d("MyActivity", "ResList index: " + i);
        }
        for (int i = 0; i < filter.size(); i++) {
            ResList.add(filter.get(i));
        }
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurant_list);
        list.setAdapter(adapter);
    }


    private void filterFavourites() {
        if(filterer.isFavSelected()){//unselect favorites
//            if(search_name.length()!=0){
//                filter = filterer.unFilterFavorites(filter);
//                Log.d("Filter", "Searched Text: "+search_name);
//            }
            filter = filterer.unFilterFavorites();
        }
        else{
            filter = filterer.filterFavourites(filter);
        }
        populateList();
    }

    private void setupFavouritesButton() {
        final Button favouritesButton = findViewById(R.id.favouritesListBtn);
        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFavourites();
                if(filterer.isFavSelected()){
                    //change button color
                    favouritesButton.setTextColor(Color.parseColor("#083c99"));
                }
                else{
                    favouritesButton.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

    }

    private void filterHazard(InspectionReport.HazardRating hazard) {
        filterer.setHazard(hazard);
        filter = filterer.filterHazard(filter);
        Button btn = findViewById(R.id.hazardListBtn);
        btn.setTextColor(Color.parseColor("#083c99"));
        populateList();

    }

    private void showHazardPopup() {
        final android.app.AlertDialog.Builder dialogBuilder;
        final android.app.AlertDialog dialog;

        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.filter_hazard_dialog_box, null);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        Button lowButton = contactPopupView.findViewById(R.id.lowBtn);
        Button moderateButton = contactPopupView.findViewById(R.id.moderateBtn);
        Button highButton = contactPopupView.findViewById(R.id.highBtn);
        Button clear = contactPopupView.findViewById(R.id.clear);


        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked(0)) {
                    populateFilter();
                }
                clicked(0);
                filterHazard(InspectionReport.HazardRating.LOW);
                dialog.dismiss();
            }
        });

        moderateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked(0)) {
                    populateFilter();
                }
                clicked(0);
                filterHazard(InspectionReport.HazardRating.MODERATE);
                dialog.dismiss();
            }
        });

        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked(0)) {
                    populateFilter();
                }
                clicked(0);
                filterHazard(InspectionReport.HazardRating.HIGH);
                dialog.dismiss();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = filterer.unFilterHazard();
                Button btn = findViewById(R.id.hazardListBtn);
                btn.setTextColor(Color.parseColor("#000000"));
                populateList();
                dialog.dismiss();
            }
        });
    }

    private void setupHazardButton() {
        final Button hazardButton = findViewById(R.id.hazardListBtn);
        hazardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHazardPopup();
                if(filterer.isHazardSelected()){
                    hazardButton.setTextColor(Color.parseColor("#083c99"));
                }
                else{
                    hazardButton.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
    }

    private void showViolationsPopup() {
        final AlertDialog.Builder dialogBuilder;
        final AlertDialog dialog;

        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.filter_violations_dialog_box, null);

        final Spinner spinner = contactPopupView.findViewById(R.id.lessOrGreater);

        List<String> categories = new ArrayList<>();
        categories.add("<=");
        categories.add(">=");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categories);

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        final EditText criticalViolationsText = contactPopupView.findViewById(R.id.violationsEditTxt);
        criticalViolationsText.setHint(getString(R.string.text_hint));

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        Button enterButton = contactPopupView.findViewById(R.id.enterBtn);
        Button clear = contactPopupView.findViewById(R.id.clear2);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (criticalViolationsText.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.length_zero), Toast.LENGTH_SHORT)
                            .show();
                    dialog.dismiss();
                    return;
                }
                if (isClicked(1)) {
                    populateFilter();
                }
                clicked(1);
                int criticalViolations = Integer.parseInt(criticalViolationsText.getText().toString());
                boolean flag = (spinner.getSelectedItemPosition() == 1);
                filterViolations(flag, criticalViolations);
                dialog.dismiss();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = filterer.unFilterViolations();
                populateList();
                Button btn = findViewById(R.id.violationsListBtn);
                btn.setTextColor(Color.parseColor("#000000"));
                dialog.dismiss();
            }
        });

    }

    private void filterViolations(boolean flag, int criticalViolations) {
        Filter filterer = new Filter();
        filterer.setGreaterThanOrEqualTo(flag);
        filterer.setCriticalViolations(criticalViolations);
        filter = filterer.filterViolations(filter);
        Log.d("FILTER", "flag: "+ flag+"# violations set: " + criticalViolations);
        populateList();
        Button btn = findViewById(R.id.violationsListBtn);
        btn.setTextColor(Color.parseColor("#083c99"));
    }

    private void setupViolationsButton() {
        final Button violationsButton = findViewById(R.id.violationsListBtn);
        violationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViolationsPopup();
                if(filterer.isViolSelected()){
                    violationsButton.setTextColor(Color.parseColor("#083c99"));
                }
                else{
                    violationsButton.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
    }

    private void setupResetButton() {
        Button resetButton = findViewById(R.id.resetListBtn);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateListView();
                unclicked(0);
                unclicked(1);
                filterer.setFavSelected(false);
                Button btn3 = findViewById(R.id.favouritesListBtn);
                btn3.setTextColor(Color.parseColor("#000000"));
                filterer.setHazardSelected(false);
                Button btn = findViewById(R.id.hazardListBtn);
                btn.setTextColor(Color.parseColor("#000000"));
                filterer.setViolSelected(false);
                Button btn2 = findViewById(R.id.violationsListBtn);
                btn2.setTextColor(Color.parseColor("#000000"));
            }
        });
    }


    private void search() {
        SearchView searchView=findViewById(R.id.search_main);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
               search_name=newText;

                filter = sorting(filter);
                updateUI();

                return false;
            }
        });
    }
    public static ArrayList<Restaurant> sorting(ArrayList<Restaurant> filter) {
        ResList.clear();
         //ResList = new ArrayList<Restaurant>();
        RestaurantManager manager=RestaurantManager.getInstance();
        ArrayList<Restaurant> newFilter = new ArrayList<>();

        for(int i=0;i<filter.size();i++){
            //Restaurant restaurant=manager.getRestFromIndex(i);
            Restaurant restaurant = filter.get(i);
            if(restaurant.getName().toLowerCase().contains(search_name.toLowerCase())){
                ResList.add(restaurant);
                newFilter.add(restaurant);
            }
            else
                continue;

        }
        return newFilter;
    }

    private void updateUI() {

        MyListAdapter adapter=new MyListAdapter(MainActivity.this,R.layout.activity_main,ResList);
        list.setAdapter(adapter);

    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void createMapIntent(boolean isOpened) {
        if (isServicesOK()) {
            Intent intent = MapsActivity.launchIntent(this, isOpened);
            startActivity(intent);
            mapIsOpened = isOpened;
            //finish();  //<- this makes the app close, not sure why it's there
        }
    }

    private void setUpMapViewButton() {
        Button button = findViewById(R.id.mapViewBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMapIntent(true);
            }
        });
    }

    private int findRestaurantIndex(int index) {
        int result = -1;
        String trackingNum = filter.get(index).getTrackingNum();
        for (int i = 0; i < restaurants.getSize(); i++) {
            if(restaurants.getRestFromIndex(i).getTrackingNum() == trackingNum)
                result = i;
        }

        return result;
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

    private void getFavsFromSharedPref(){
        Log.d("MyActivity", "RUNNING GETFAVSFROMSHAREDPREFS");
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Gson lensGson = new Gson();
        String lensJson = prefs.getString(PREFS_FAVORITES,null );
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> favIDs = lensGson.fromJson(lensJson, type);

        if(favIDs != null) {
            for(int i = 0; i < favIDs.size(); i++){
                String ID = favIDs.get(i);
                Restaurant r = restaurants.getRestFromTracking(ID);
                r.setFav(true);
                Log.d("MyActivity", "ID: "+ID);
            }
        }
    }

    private void registerClickCallback() {
        list = findViewById(R.id.restaurant_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = RestaurantDetail.launchIntent(MainActivity.this, findRestaurantIndex(position));
                startActivity(intent);
            }
        });
    }

    public void populateListView() {
        populateFilter();
        if(populated == 0){
            Log.d("MyActivity", "populated = 0");
            getFavsFromSharedPref();
            populated++;
        }
        //clear what was before first
        int resListSize = ResList.size();
        for(int i = 0; i < resListSize; i++){
            ResList.remove(0);
        }
        for (int i = 0; i < filter.size(); i++) {
            ResList.add(filter.get(i));
        }
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurant_list);
        list.setAdapter(adapter);

    }

    private class MyListAdapter extends ArrayAdapter<Restaurant>{
        public MyListAdapter(){
            super(MainActivity.this, R.layout.listview_each_restaurant, ResList);
        }
        public MyListAdapter(Context context,int resource,List<Restaurant>restaurantList){
            super(MainActivity.this, resource, restaurantList);
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

            ImageView restaurantLogo = itemView.findViewById(R.id.restaurant_image_icon);
            String resourceId = currentRestaurant.getRes_id();

            int resId = MainActivity.this.getResources().getIdentifier(
                    resourceId,
                    "drawable",
                    MainActivity.this.getPackageName()
            );
            if (resId == 0) {
                restaurantLogo.setImageResource(R.drawable.restaurant);
            } else {
                restaurantLogo.setImageResource(resId);
            }

            ImageView fav = itemView.findViewById(R.id.favorite);
            if(currentRestaurant.isFav()){
                fav.setImageResource(android.R.drawable.btn_star_big_on);
            }
            else{
                fav.setImageResource(android.R.drawable.btn_star_big_off);
            }

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

            int lastInspectionID = R.string.restaurant_inspectionPerformedOn;
            TextView dateText = itemView.findViewById(R.id.restaurant_label_latestInspection);
            if(inspections.getSize()!=0){
                String date = getString(lastInspectionID)+ inspections.getInspection(0).getInspectDateString();
                dateText.setText(date);
            }
            else{
                String date = getString(lastInspectionID)+ getString(R.string.never);
                dateText.setText(date);
            }

            int issuesID = R.string.restaurant_issuesfound;
            TextView issues = itemView.findViewById(R.id.restaurant_problemsFound);
            if(inspections.getSize()!= 0){
                int problems = inspections.getInspection(0).getNumCritical() + inspections.getInspection(0).getNumNonCritical();
                String issuesText = getString(issuesID) + problems;
                issues.setText(issuesText);
            }
            else{
                String issuesText = getString(issuesID) + 0;
                issues.setText(issuesText);
            }


            return itemView;
        }

    }


}