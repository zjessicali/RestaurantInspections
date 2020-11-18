package com.example.group_9_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.group_9_project.R;

public class LoadingFragment extends AppCompatDialogFragment {
    private LoadingFragment.LoadingFragmentListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create animation
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(1200);
        ImageView image = new ImageView(getActivity());
        Bitmap bmp;
        int width = 100;
        int height = 100;
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
        bmp = Bitmap.createScaledBitmap(bmp,width,height,true);
        image.setImageBitmap(bmp);
        image.startAnimation(rotate);

        //create button listener
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onCancelClicked();
            }
        };

        //build alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Please wait...")
                .setView(image)
                .setNegativeButton("Cancel download", cancelListener )
                .setCancelable(true)
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LoadingFragment.LoadingFragmentListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "must implement LoadingFragmentListener");
        }
    }

    public interface LoadingFragmentListener{
        void onCancelClicked();
    }
}
