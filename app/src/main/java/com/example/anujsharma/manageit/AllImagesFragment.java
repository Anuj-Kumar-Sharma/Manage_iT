package com.example.anujsharma.manageit;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        allImagesRecyclerView= view.findViewById(R.id.allImagesRecyclerView);
        allImagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        allImagesRecyclerView.setAdapter(myAllImagesViewAdapter);
        return view;
    }





}
