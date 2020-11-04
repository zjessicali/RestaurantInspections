package com.example.group_9_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.group_9_project.R;
import com.example.group_9_project.model.Violation;
import com.example.group_9_project.model.ViolationManager;

public class MessageFragment extends AppCompatDialogFragment {
    private static int position;
    Violation v;
    ViolationManager manager=new ViolationManager();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.message,null);

        DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView textView=(TextView)getActivity().findViewById(R.id.dialog);
                textView.setText(manager.violationList.get(position).toString());
            }
        };
        return new AlertDialog.Builder(getActivity())
                .setTitle("Violation Detail")
                .setView(v)
                .setPositiveButton(android.R.string.ok,listener)
                .create();


    }
    public static void getposition(int position1){position=position1;}


}