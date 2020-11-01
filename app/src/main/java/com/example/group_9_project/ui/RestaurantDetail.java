package com.example.group_9_project.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.group_9_project.R;
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
        extractData();
        setupRestaurantName();
        setupRestaurantAddress();
        setupGPSCoordinates();

    }

    private void popultaeManager() {
        //DELETE THIS!!
        Restaurant restaurant = new Restaurant("12345");
        restaurant.setName("Blossom Teas");
        restaurant.setAddress("7205 Barnet Road");
        restaurant.setCity("Burnaby");
        restaurant.setLatitude(12345.1234);
        restaurant.setLongitude(2345.345);

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