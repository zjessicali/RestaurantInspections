package com.example.group_9_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.group_9_project.R;

//ask user to update
public class AskUpdateFragment extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create view
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.ask_update_layout,null);

        //create button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make a log, you clicked dialog button
            }
        };

        //build alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Update?")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }
}
