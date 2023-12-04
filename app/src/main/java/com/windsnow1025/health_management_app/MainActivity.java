package com.windsnow1025.health_management_app;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.fragment.alert.AlertFragment;
import com.windsnow1025.health_management_app.fragment.home.HomeFragment;
import com.windsnow1025.health_management_app.fragment.main.MainFragment;
import com.windsnow1025.health_management_app.fragment.shopping.ShoppingFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonMain = findViewById(R.id.buttonMain);
        Button buttonAlert = findViewById(R.id.buttonAlert);
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonShoppingCart = findViewById(R.id.buttonShoppingCart);

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

        buttonShoppingCart.setOnClickListener(view -> {
            Fragment fragment = new ShoppingFragment();
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