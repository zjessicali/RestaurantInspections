package com.example.group_9_project.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;

import org.w3c.dom.Text;

public class RestaurantDetail extends AppCompatActivity {

    //change is to manager.getInstance() when singleton support is added
    private RestaurantManager manager = new RestaurantManager();
    private int index;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        populateInspectionsList();
        extractData();
        setupRestaurantName();
        setupRestaurantAddress();
        setupGPSCoordinates();

    }

    private void populateInspectionsList() {
        InspectionManager inspections = manager.getRestFromIndex(index).getInspections();
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);

        for (int i = 0; i < inspections.getSize(); i++) {
            final InspectionReport inspection = inspections.getInspection(i);

            TextView text = new TextView(RestaurantDetail.this);
            String hazard = "";

            switch(inspection.getHazard()) {
                case LOW:
                    hazard = "<font color = '#00FF00'>LOW</font>";
                    break;

                case MODERATE:
                    hazard = "<font color = '#FFFF00'>MEDIUM</font>";
                    break;

                case HIGH:
                    hazard = "<font color = '#FF0000'>HIGH</font>";
                    break;
            }

            String report = inspection.getNumCritical() + " critical issues| "
                    + inspection.getNumNonCritical() + " non-critical issues| "
                    + inspection.getInspectDateString() + "| Hazard level:"
                    + hazard;

            text.setText(Html.fromHtml(report));
            text.setTextSize(20);

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set intent when Single Inspection is created
                }
            });

            scrollLayout.addView(text);
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