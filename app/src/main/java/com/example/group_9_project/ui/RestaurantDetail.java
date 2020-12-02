package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

//displays details about a restaurant
public class RestaurantDetail extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREFS_FAVORITES = "FavoritesPrefs";
    private RestaurantManager manager;
    private List<InspectionReport> inspections;
    private int index;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        manager = RestaurantManager.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.restaurant_detail);

        extractData();
        populateInspectionsList();
        registerClickCallback();
        setupRestaurantName();
        setupRestaurantAddress();
        setupGPSCoordinates();
        setFavBtn();
        backbutton();
    }

    private void putFavsToSharedPref(){
        Log.d("MyActivity", "RUNNING PUTFAVSTOSHAREDPREFS");
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        List<String> favIDs = new ArrayList<>();

        for(int i = 0; i < manager.getSize(); i++){
            Restaurant r = manager.getRestFromIndex(i);
            if(r.isFav()){
                favIDs.add(r.getTrackingNum());
            }
        }

        String json = gson.toJson(favIDs);
        editor.putString(PREFS_FAVORITES, json);
        editor.apply();
    }

    private void setFavBtn() {
        final Button btn = findViewById(R.id.favBtn);
        final Restaurant rest = manager.getRestFromIndex(index);

        if (rest.isFav()) {
            btn.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!rest.isFav()){//not fav, set fav
                    rest.setFav(true);
                    btn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                }
                else{//unfavorite
                    rest.setFav(false);
                    btn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                }
                putFavsToSharedPref();
                MainActivity.getInstance().populateListView();
            }
        });

    }

    private void backbutton() {
        getSupportActionBar().setTitle(R.string.restaurant_detail)
        ;
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void populateInspectionsList() {
        inspections = new ArrayList<>();
        InspectionManager inspectionManager = manager.getRestFromIndex(index).getInspections();
        for(int i = 0; i < inspectionManager.getSize(); i++) {
            inspections.add(inspectionManager.getInspection(i));
        }

        ArrayAdapter<InspectionReport> adapter = new MyListAdapter();
        ListView inspectionsList = findViewById(R.id.inspectionsList);
        inspectionsList.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView inspectionsList = findViewById(R.id.inspectionsList);
        inspectionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = InspectionDetail.launchIntent(RestaurantDetail.this, index, position);
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<InspectionReport> {


        public MyListAdapter() {
            super(RestaurantDetail.this,
                    R.layout.inspection_list_items,
                    inspections);
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_list_items, parent, false);
            }

            InspectionReport currentInspection = inspections.get(position);
            ImageView hazard = itemView.findViewById(R.id.hazardImageView);
            TextView inspectionDateText = itemView.findViewById(R.id.inspectionDate);
            TextView numCriticalText = itemView.findViewById(R.id.criticalText);
            TextView numNonCriticalText = itemView.findViewById(R.id.nonCriticalText);

            switch(currentInspection.getHazard()) {
                case LOW:
                    hazard.setImageResource(R.drawable.low_risk);
                    hazard.setContentDescription(getString(R.string.low_content_description));
                    break;

                case MODERATE:
                    hazard.setImageResource(R.drawable.medium_risk);
                    hazard.setContentDescription(getString(R.string.moderate_content_description));
                    break;

                case HIGH:
                    hazard.setImageResource(R.drawable.high_risk);
                    hazard.setContentDescription(getString(R.string.high_content_description));
                    break;
            }

            //String numCritical = String.format("%s%d", getResources().getString(R.string.num_critical), currentInspection.getNumCritical());
            String numCritical = getString(R.string.num_critical) + currentInspection.getNumCritical();
            numCriticalText.setText(numCritical);

            //String numNonCritical = String.format("%s%d", getResources().getString(R.string.num__non_critical), currentInspection.getNumNonCritical());
            String numNonCritical = getString(R.string.num__non_critical) + currentInspection.getNumNonCritical();
            numNonCriticalText.setText(numNonCritical);

            inspectionDateText.setText(currentInspection.getInspectDateString());

            return itemView;
        }
    }

    public static Intent launchIntent(Context context, int index) {
        Intent intent = new Intent(context, RestaurantDetail.class);
        intent.putExtra("Index", index);
        return intent;
    }

    private void extractData() {
        Intent intent = getIntent();
        index = intent.getIntExtra("Index", 0);
    }

    private void setupGPSCoordinates() {
        TextView coordinateText = findViewById(R.id.gpsCoord);
        String gpsCoordinate = manager.getRestFromIndex(index).getLatitude() + ", "
                + manager.getRestFromIndex(index).getLongitude();
        coordinateText.setText(gpsCoordinate);
        coordinateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=MapsActivity.makeIntent(RestaurantDetail.this,index,new LatLng(manager.getRestFromIndex(index).getLatitude(),manager.getRestFromIndex(index).getLongitude()));
                startActivity(intent);

            }
        });
    }

    private void setupRestaurantAddress() {
        TextView restaurantAddress = findViewById(R.id.restaurantAddress);
        restaurantAddress.setText(manager.getRestFromIndex(index).getAddress());
    }

    private void setupRestaurantName() {
        TextView restaurantName = findViewById(R.id.restaurantName);
        restaurantName.setText(manager.getRestFromIndex(index).getName());
    }
}