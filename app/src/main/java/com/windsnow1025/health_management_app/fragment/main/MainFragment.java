package com.windsnow1025.health_management_app.fragment.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.model.User;
import com.windsnow1025.health_management_app.database.UserLocalDao;

// Logcat Filter:
// package:com.windsnow1025.health_management_app -tag:chromium -tag:OpenGLRenderer -tag:EGL_emulation

public class MainFragment extends Fragment {
    View view;
    private LeftNavigationFragment leftNavigation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        // Init user
        User userInfo = new User();
        UserLocalDao userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();

        // Get Sex
        String gender = "male";
        String phoneNumber;
        phoneNumber = userLocalDao.getPhoneNumber();
        Log.i("test", "这里把从服务器获取数据删了，从本地获取用户数据");
        userInfo = userLocalDao.getUserInfo(phoneNumber);
        gender = userInfo.getSex();


        // Set Image
        ImageView imageAnatomy = view.findViewById(R.id.imageAnatomy);
        imageAnatomy.scrollBy(0, 0);
        if (gender.equals("female")) {
            imageAnatomy.setImageResource(R.drawable.female);
        } else {
            imageAnatomy.setImageResource(R.drawable.male);
        }


        Button buttonInside = view.findViewById(R.id.buttonInside);

        buttonInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new Main1Fragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Left Navigation
        leftNavigation = new LeftNavigationFragment();
        FragmentTransaction transactionLeft = getParentFragmentManager().beginTransaction();
        transactionLeft.add(R.id.layoutLeftNavigation, leftNavigation);
        transactionLeft.commit();

        // Button Navigation
        Button buttonNavigation = view.findViewById(R.id.buttonNavigation);
        buttonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftNavigation.isVisible()) {
                    leftNavigation.getView().setVisibility(View.GONE);
                } else {
                    leftNavigation.getView().setVisibility(View.VISIBLE);
                }
            }
        });

        // Button Page
        FragmentTransaction transactionButton = getParentFragmentManager().beginTransaction();
        transactionButton.replace(R.id.frameLayoutButtonPage, new ButtonPageFragment());
        transactionButton.addToBackStack(null);
        transactionButton.commit();

        return view;
    }
}
