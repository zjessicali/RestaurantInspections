package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

//displays details about a restaurant
public class RestaurantDetail extends AppCompatActivity {

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
        backbutton();
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
                    hazard.setContentDescription("Low Hazard");
                    break;

                case MODERATE:
                    hazard.setImageResource(R.drawable.medium_risk);
                    hazard.setContentDescription("Moderate Hazard");
                    break;

                case HIGH:
                    hazard.setImageResource(R.drawable.high_risk);
                    hazard.setContentDescription("High Hazard");
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