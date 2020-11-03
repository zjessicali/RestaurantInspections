package com.example.group_9_project.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

        popultaeManager(); //DELETE THIS
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
            InspectionReport inspection = inspections.getInspection(i);
            String report = inspection.getNumCritical() + " critical issues     "
                    + inspection.getNumNonCritical() + " non-critical issues     "
                    + inspection.getInspectDateString();


            TextView text = new TextView(RestaurantDetail.this);
            text.setText(report);

            switch(inspection.getHazard()) {
                case LOW:
                    text.setTextColor(Color.GREEN);
                    break;

                case MODERATE:
                    text.setTextColor(Color.YELLOW);
                    break;

                case HIGH:
                    text.setTextColor(Color.RED);
                    break;
            }

            scrollLayout.addView(text);
        }
        
    }

    private void popultaeManager() {
        //DELETE THIS!!
        Restaurant restaurant = new Restaurant("12345");
        restaurant.setName("Blossom Teas");
        restaurant.setAddress("7205 Barnet Road");
        restaurant.setCity("Burnaby");
        restaurant.setLatitude(12345.1234);
        restaurant.setLongitude(2345.345);

        InspectionManager inspections = new InspectionManager();
        InspectionReport report1 = new InspectionReport();
        report1.setHazard(InspectionReport.HazardRating.HIGH);
        report1.setInspectDate(20200901);
        report1.setInspType(InspectionReport.InspType.FOLLOWUP);
        report1.setNumCritical(10);
        report1.setNumNonCritical(2);

        InspectionReport report2 = new InspectionReport();
        report2.setHazard(InspectionReport.HazardRating.LOW);
        report2.setInspectDate(20201028);
        report2.setInspType(InspectionReport.InspType.ROUTINE);
        report2.setNumCritical(0);
        report2.setNumNonCritical(12);

        inspections.addInspection(report1);
        restaurant.addInspection(report1);

        inspections.addInspection(report2);
        restaurant.addInspection(report2);

        manager.addRestaurant(restaurant);
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