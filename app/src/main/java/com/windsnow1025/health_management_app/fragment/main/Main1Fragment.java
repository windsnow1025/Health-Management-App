package com.windsnow1025.health_management_app.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;

public class Main1Fragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_1, container, false);

        // Button Page
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutButtonPage, new ButtonPageFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        Button buttonInside2 = view.findViewById(R.id.buttonInside2);
        Button buttonOutside = view.findViewById(R.id.buttonOutside);

        buttonInside2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new Main2Fragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonOutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button buttonBrain = view.findViewById(R.id.buttonBrain);
        Button buttonRespiratory = view.findViewById(R.id.buttonRespiratory);
        Button buttonUrinary = view.findViewById(R.id.buttonRenal);

        buttonBrain.setOnClickListener(v -> {
            FragmentTransaction transaction1 = getParentFragmentManager().beginTransaction();
            transaction1.replace(R.id.fragment_container, new MainOrganFragment("brain"));
            transaction1.addToBackStack(null);
            transaction1.commit();
        });

        buttonRespiratory.setOnClickListener(v -> {
            FragmentTransaction transaction12 = getParentFragmentManager().beginTransaction();
            transaction12.replace(R.id.fragment_container, new MainOrganFragment("respiratory"));
            transaction12.addToBackStack(null);
            transaction12.commit();
        });

        buttonUrinary.setOnClickListener(v -> {
            FragmentTransaction transaction13 = getParentFragmentManager().beginTransaction();
            transaction13.replace(R.id.fragment_container, new MainOrganFragment("urinary"));
            transaction13.addToBackStack(null);
            transaction13.commit();
        });

        return view;
    }
}
