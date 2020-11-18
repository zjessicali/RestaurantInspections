package com.example.group_9_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.group_9_project.R;
import com.example.group_9_project.model.UpdateData;

//ask user to update
public class AskUpdateFragment extends AppCompatDialogFragment {

    private UpdateData updateData = UpdateData.getInstance();
    private AskUpdateListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create view
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.ask_update_layout,null);

        //create button listener
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make a log, you clicked dialog button
                Log.d("MyActivity","You clicked ok ... update....");
                listener.startUpdate();
            }
        };

        DialogInterface.OnClickListener noListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make a log, you clicked dialog button
                Log.d("MyActivity","You clicked no, do not update... change needUpdate....");

            }
        };

        //build alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Update?")
                .setView(v)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.no, noListener)
                .create();
    }

    //https://codinginflow.com/tutorials/android/custom-dialog-interface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AskUpdateListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "must implement AskUpdateListener");
        }
    }

    public interface AskUpdateListener{
        void startUpdate();
    }
}
