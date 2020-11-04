package com.example.group_9_project.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.Violation;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.group_9_project.R;
import com.example.group_9_project.model.InspectionReport;
import com.example.group_9_project.model.ViolationManager;

public class Story3 extends AppCompatActivity {
    private static InspectionReport report;
    ViolationManager manager = report.getManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story3);
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
        if(report.getHazard().equals("LOW")) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageResource(R.drawable.green);
        }
        else if(report.getHazard().equals("MODERATE")){
            ImageView imageView=(ImageView)findViewById(R.id.imageView2);
            imageView.setImageResource(R.drawable.orange);
        }
        else{
            ImageView imageView=(ImageView)findViewById(R.id.imageView2);
            imageView.setImageResource(R.drawable.red);

        }

    }

    private void regiterclick() {
        ListView list = (ListView) findViewById(R.id.ArrayList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation clicked = manager.violationList.get(position);
                MessageFragment.getposition(position);
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
            super(Story3.this, R.layout.list_items, manager.violationList);


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemview = convertView;
            if (itemview == null) {
                itemview = getLayoutInflater().inflate(R.layout.list_items, parent, false);
            }
            Violation violation=manager.violationList.get(position) ;
            TextView textView = (TextView) findViewById(R.id.list_violation_header);
            textView.setText(violation.toString());
            return itemview;
        }


    }

    public static Intent LaunchIntent(Context c, InspectionReport repo) {
        report = repo;
        Intent intent = new Intent(c, Story3.class);
        return intent;
    }



}