package com.example.group_9_project.ui;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

public class DataFragment extends Fragment {
    public static DataFragment newInstance() {
        return new DataFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
//        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
//        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        return v;
//    }
}
