package com.example.anujsharma.manageit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AllImagesFragment extends Fragment {

    View view;
    MyAllImagesViewAdapter myAllImagesViewAdapter;
    RecyclerView allImagesRecyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_all_images, container, false);
        myAllImagesViewAdapter=new MyAllImagesViewAdapter(getContext());
        allImagesRecyclerView= (RecyclerView) view.findViewById(R.id.allImagesRecyclerView);
        allImagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        allImagesRecyclerView.setAdapter(myAllImagesViewAdapter);
        return view;
    }





}
