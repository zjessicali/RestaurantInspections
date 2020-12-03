package com.example.group_9_project.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.RestaurantManager;
import com.example.group_9_project.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for showing favorite restaurant with updates in a listView
 */

public class FavList extends AppCompatActivity {

    static List<Restaurant> favRestaurantList = new ArrayList<>();
    static List<String> favResTrackNumList = new ArrayList<>();
    static RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private static final String TAG = "FavList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_restaurant_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.favorite_restaurants_with_new_inspections);

        getFavUpdatedList();
        populateListView();
        setUpButton();

    }

    private void setUpButton() {
        Button btn = findViewById(R.id.favExit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getFavUpdatedList() {


        for(Restaurant r: restaurantManager.getRestaurants()){
            if (r.isFav()) {
                int beforeUpdate = getDatesFromSharedPref(r.getTrackingNum());
                if(beforeUpdate!= 0 && beforeUpdate<r.getInspections().getLastInspDate()){//0 means either no insp, or not favorited
                    favRestaurantList.add(r);
                }
            }
        }
        Log.d(TAG, "getFavResList: favRestaurantList: "+favRestaurantList);
    }

    //    populate List of Restaurants

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.fav_list);
        list.setAdapter(adapter);
    }


    //    ListView adapter build
    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter(){
            super(FavList.this, R.layout.listview_each_fav_restaurant, favRestaurantList);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.listview_each_fav_restaurant, parent, false);
            }

            Restaurant curr_r = favRestaurantList.get(position);
            TextView name = itemView.findViewById(R.id.fav_restaurant_name);
            name.setText(curr_r.getName());

            InspectionManager inspections = curr_r.getInspections();
            TextView recentIns = itemView.findViewById(R.id.fav_latestInspection);
            String display;
           // **********************************
            int lastInspectionID = R.string.restaurant_inspectionPerformedOn;
            //TextView dateText = itemView.findViewById(R.id.restaurant_label_latestInspection);
            if(inspections.getSize()!=0){
                String date = getString(lastInspectionID)+ inspections.getInspection(0).getInspectDateString();
                //dateText.setText(date);
                display = date;
            }
            else{
                String date = getString(lastInspectionID)+ getString(R.string.never);
                //dateText.setText(date);
                display= date;
            }
            recentIns.setText(display);
           //***********************************

           /* if (inspections.getListSize() != 0) {
                String inspectionDate = curr_r.getLatest().getFormattedDate(com.example.group_9_project.ui.FavList.this);
                display = String.format(
                        getResources().getString(R.string.restaurant_inspectionPerformedOn),
                        inspectionDate);
            } else {
                display = String.format(
                        getResources().getString(R.string.restaurant_inspectionPerformedOn),
                        (getResources().getString(R.string.restaurant_noInspectionAvailable_value)));
            }

            */
            ImageView hazard_icon = itemView.findViewById(R.id.fav_hazard_icon);

            if(inspections.getSize() != 0){
                switch(inspections.getInspection(0).getHazard()){
                    case LOW:
                        hazard_icon.setImageResource(R.drawable.low_risk);
                        itemView.setBackground(getDrawable(R.drawable.low_back));
//                        display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
//                                getResources().getString(R.string.restaurant_hazardLevelLow_value));
                        break;
                    case MODERATE:
                        hazard_icon.setImageResource(R.drawable.medium_risk);
                        itemView.setBackground(getDrawable(R.drawable.mid_back));
//                        display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
//                                getResources().getString(R.string.restaurant_hazardLevelModerate_value));
                        break;
                    case HIGH:
                        hazard_icon.setImageResource(R.drawable.high_risk);
                        itemView.setBackground(getDrawable(R.drawable.high_back));
//                        display = String.format(getResources().getString(R.string.restaurant_hazardlevel),
//                                getResources().getString(R.string.restaurant_hazardLevelHigh_value));
                        break;
                }
            }
//            no inspection found
            else {
                hazard_icon.setImageResource(R.drawable.low_risk);
                itemView.setBackgroundColor(0);
//                display = String.format(getResources().getString(R.string.restaurant_hazardLevel),
//                        getResources().getString(R.string.restaurant_hazardLevelNo_value));
            }
            //hazard.setText(display);


            return itemView;
        }

    }
    public static Intent makeIntent(Context c){
        Intent i = new Intent(c, FavList.class);

        return i;
    }

    private int getDatesFromSharedPref(String ID){
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt(ID, 0);
    }

    private void putDatesSharedPref(String ID, int date){
        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(ID, date);
        editor.apply();
    }

}

