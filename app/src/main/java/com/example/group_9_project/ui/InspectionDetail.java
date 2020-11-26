package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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


//displays details about an inspection
public class InspectionDetail extends AppCompatActivity {
    private static InspectionReport report;
    private static ViolationManager manager;
    private RestaurantManager restaurants;
    private int restaurantIndex;
    private int inspectionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story3);
        restaurants = RestaurantManager.getInstance();
        manager = report.getManager();
        setContentView(R.layout.activity_story3);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.inspection_detail);

        setarrayadapter();
        populateHeader();
        regiterclick();
        bsckbutton();
    }

    private void bsckbutton() {
        getSupportActionBar().setTitle(R.string.inspections);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void populateHeader() {
        TextView textView = (TextView) findViewById(R.id.InspectionName);
        textView.setText("" + report.getFullDate());
        TextView textView1 = (TextView) findViewById(R.id.InspectionType);
        textView1.setText("" + report.getInspType());
        TextView textView2 = (TextView) findViewById(R.id.Severity);
        textView2.setText("" + report.getHazard());
        TextView textView3=(TextView)findViewById(R.id.Critical);
        String issues = R.string.critical_issues + report.getNumCritical()
                + " " + R.string.non_critical_issues + report.getNumNonCritical();
        textView3.setText(issues);


      if(report.getHazard().equals(InspectionReport.HazardRating.HIGH)) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageResource(R.drawable.high_risk);
            textView2.setTextColor(Color.RED);
        }
        else if(report.getHazard().equals(InspectionReport.HazardRating.MODERATE)){
            ImageView imageView=(ImageView)findViewById(R.id.imageView2);
            imageView.setImageResource(R.drawable.medium_risk);
            textView2.setTextColor( Color. rgb(255, 165, 0));
        }
       else{
           ImageView imageView=(ImageView)findViewById(R.id.imageView2);
            imageView.setImageResource(R.drawable.low_risk);
            textView2.setTextColor(Color.rgb(51,204,90));
        }

    }

    private void regiterclick() {
        ListView list = (ListView) findViewById(R.id.ArrayList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageFragment.getposition(position,manager);
                Violation clicked = manager.getViolLump().get(position);
                MessageFragment.getposition(position,manager);
                FragmentManager manager = getSupportFragmentManager();
                MessageFragment dialog = new MessageFragment();
                dialog.show(manager, "Message");
                Log.i("Tag", "Showed Dialog");
            }
        });
    }

    private void setarrayadapter() {
        ArrayAdapter<Violation> adapter = new MyListAdapter();
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
            textView.setText(violation.getViolTypeString());
            if(violation.getCritical().equals("Critical")) {
                ImageView imageView = itemview.findViewById(R.id.imageView_critical);
                imageView.setImageResource(R.drawable.critical);
            }
            else {
                ImageView imageView1=itemview.findViewById(R.id.imageView_critical);
                imageView1.setImageResource(R.drawable.not_critical);
            }


            if(violation.getViolTypeString().equals("Food")){
                ImageView imageView=itemview.findViewById(R.id.imageView_list);
                imageView.setImageResource(R.drawable.food);
            }
            else if(violation.getViolTypeString().equals("Equipment")){
                ImageView imageView1=itemview.findViewById(R.id.imageView_list);
                imageView1.setImageResource(R.drawable.equipment);
            }
            else if(violation.getViolTypeString().equals("Chemical")){
                ImageView imageView1=itemview.findViewById(R.id.imageView_list);
                imageView1.setImageResource(R.drawable.chemical);
            }
            else if(violation.getViolTypeString().equals("Containers")){
                ImageView imageView2=itemview.findViewById(R.id.imageView_list);
                imageView2.setImageResource(R.drawable.utensils);
            }
            else if(violation.getViolTypeString().equals("Location")){
                ImageView imageView3=itemview.findViewById(R.id.imageView_list);
                imageView3.setImageResource(R.drawable.location);
            }
            else if(violation.getViolTypeString().equals("Requirements")){
                ImageView imageView4=itemview.findViewById(R.id.imageView_list);
                imageView4.setImageResource(R.drawable.requirement);
            }
            else {
                ImageView imageView5=itemview.findViewById(R.id.imageView_list);
                imageView5.setImageResource(R.drawable.hygiene);
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
        report=inspections.getInspection(inspectionIndex);
        if (inspections.getSize() != 0)
            intent.putExtra("inspectionIndex", inspectionIndex);

        return intent;
    }

    private void extractData() {
        Intent intent = getIntent();
        restaurantIndex = intent.getIntExtra("restaurantIndex", 0);

        RestaurantManager restaurants = RestaurantManager.getInstance();
        Restaurant restaurant = restaurants.getRestFromIndex(restaurantIndex);
        InspectionManager inspections = restaurant.getInspections();

        if (inspections.getSize() != 0)
            inspectionIndex = intent.getIntExtra("inspectionindex", 0);
    }
}