package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionManager;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.Restaurant;
import com.example.group_9_project.model.RestaurantManager;
import com.example.group_9_project.model.Violation;
import com.example.group_9_project.model.ViolationManager;

public class InspectionDetail extends AppCompatActivity {

    private int restaurantIndex;
    private int inspectionIndex;
    private static InspectionReport report;
    private ViolationManager manager;
    private RestaurantManager restaurants = RestaurantManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story3);

        extractData();
        setarrayadapter();
        populateHeader();
        regiterclick();
    }

    private void populateHeader() {
        TextView textView = (TextView) findViewById(R.id.InspectionName);
        textView.setText("" + report.getFullDate());
        TextView textView1 = (TextView) findViewById(R.id.InspectionType);
        textView1.setText("" + report.getInspType());
        TextView textView2 = (TextView) findViewById(R.id.Severity);
        textView2.setText("" + report.getHazard());
//        if(report.getHazard().equals(InspectionReport.HazardRating.HIGH)) {
//            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
//            imageView.setImageResource(R.drawable.green);
//            textView2.setTextColor(Color.GREEN);
//        }
//        else if(report.getHazard().equals(InspectionReport.HazardRating.MODERATE)){
//            ImageView imageView=(ImageView)findViewById(R.id.imageView2);
//            imageView.setImageResource(R.drawable.orange);
//            textView2.setTextColor(Color.YELLOW);
//        }
//        else{
//            ImageView imageView=(ImageView)findViewById(R.id.imageView2);
//            imageView.setImageResource(R.drawable.red);
//            textView2.setTextColor(Color.RED);
//        }

    }

    private void regiterclick() {
        ListView list = (ListView) findViewById(R.id.ArrayList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation clicked = manager.getViolLump().get(position);
                MessageFragment.getposition(position);
                FragmentManager manager = getSupportFragmentManager();
                MessageFragment dialog = new MessageFragment();
                dialog.show(manager, "Message");
                Log.i("Tag", "Showed Dialog");
            }
        });
    }

    private void setarrayadapter() {
        ArrayAdapter<Violation> adapter = new InspectionDetail.MyListAdapter();
        ListView list = (ListView) findViewById(R.id.ArrayList);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {
        public MyListAdapter() {
            super(InspectionDetail.this, R.layout.list_items, manager.getViolLump());


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemview = convertView;
            if (itemview == null) {
                itemview = getLayoutInflater().inflate(R.layout.list_items, parent, false);
            }
            Violation violation=manager.getViolLump().get(position) ;
            TextView textView = itemview.findViewById(R.id.list_violation_header);
            textView.setText(violation.toString());
            if(violation.getViolTypeString().equals("FOOD")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.food);
            }
            else if(violation.getViolTypeString().equals("EQUIPMENT")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.equipment);
            }
            else if(violation.getViolTypeString().equals("CONTAINER")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.utensils);
            }
            else if(violation.getViolTypeString().equals("LOCATION")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.location);
            }
            else if(violation.getViolTypeString().equals("REQUIREMENT")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.requirement);
            }
            else if(violation.getViolTypeString().equals("HYGIENE")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.hygiene);
            }
            return itemview;
        }


    }

    public static Intent launchIntent(Context c, int restaurantIndex, int inspectionIndex) {
        Intent intent = new Intent(c, InspectionDetail.class);
        intent.putExtra("restaurantIndex", restaurantIndex);

        RestaurantManager restaurants = RestaurantManager.getInstance();
        Restaurant restaurant = restaurants.getRestFromIndex(restaurantIndex);
        InspectionManager inspections = restaurant.getInspections();
        intent.putExtra("inspectionIndex", inspectionIndex);

        return intent;
    }

    private void extractData() {
        Intent intent = getIntent();
        restaurantIndex = intent.getIntExtra("restaurantIndex", 0);
        inspectionIndex = intent.getIntExtra("inspectionindex", 0);

        RestaurantManager restaurants = RestaurantManager.getInstance();
        Restaurant restaurant = restaurants.getRestFromIndex(restaurantIndex);
        InspectionManager inspections = restaurant.getInspections();

        report = inspections.getInspection(inspectionIndex);
        manager = report.getManager();

    }
}