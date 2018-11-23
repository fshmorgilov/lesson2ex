package com.example.fshmo.businesscard.activities.intro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fshmo.businesscard.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IntroAboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_main, container, false);
        ImageView imageView = view.findViewById(R.id.intro_details_fragment_image_view);
        Glide.with(getActivity())
                .load(R.drawable.intro_screen)
                .into(imageView);
        return view;
    }
}
