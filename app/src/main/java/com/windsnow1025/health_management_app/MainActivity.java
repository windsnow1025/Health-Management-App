package com.windsnow1025.health_management_app;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.fragment.AlertFragment;
import com.windsnow1025.health_management_app.fragment.HealthFragment;
import com.windsnow1025.health_management_app.fragment.HomeFragment;
import com.windsnow1025.health_management_app.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonMain = findViewById(R.id.buttonMain);
        Button buttonAlert = findViewById(R.id.buttonAlert);
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonHealth = findViewById(R.id.buttonHealth);

        buttonMain.setOnClickListener(view -> {
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonAlert.setOnClickListener(view -> {
            Fragment fragment = new AlertFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonHealth.setOnClickListener(view -> {
            Fragment fragment = new HealthFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonHome.setOnClickListener(view -> {
            Fragment fragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonMain.performClick();
    }
}