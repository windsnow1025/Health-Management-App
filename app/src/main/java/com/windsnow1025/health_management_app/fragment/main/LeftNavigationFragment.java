package com.windsnow1025.health_management_app.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.fragment.main.MainOrganFragment;
import com.windsnow1025.health_management_app.fragment.main.Main1Fragment;
import com.windsnow1025.health_management_app.fragment.main.Main2Fragment;
import com.windsnow1025.health_management_app.fragment.main.Main3Fragment;

public class LeftNavigationFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.left_navigation, container, false);

        Button buttonBrain = view.findViewById(R.id.buttonBrain);
        Button buttonRespiratory = view.findViewById(R.id.buttonRespiratory);
        Button buttonRenal = view.findViewById(R.id.buttonRenal);
        Button buttonLiver = view.findViewById(R.id.buttonLiver);
        Button buttonDigestive = view.findViewById(R.id.buttonDigestive);
        Button buttonMusculoskeletal = view.findViewById(R.id.buttonMusculoskeletal);
        Button buttonCardiovascular = view.findViewById(R.id.buttonCardiovascular);

        Button buttonPage1 = view.findViewById(R.id.buttonPage1);
        Button buttonPage2 = view.findViewById(R.id.buttonPage2);
        Button buttonPage3 = view.findViewById(R.id.buttonPage3);

        buttonBrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("brain"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonRespiratory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("respiratory"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonRenal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("renal"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonLiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("liver"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonDigestive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("digestive"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonMusculoskeletal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("musculoskeletal"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonCardiovascular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment("cardiovascular"));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonPage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new Main1Fragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new Main2Fragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonPage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new Main3Fragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
