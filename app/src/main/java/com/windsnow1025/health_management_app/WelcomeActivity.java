package com.windsnow1025.health_management_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.windsnow1025.health_management_app.database.UserLocalDao;
import com.windsnow1025.health_management_app.utils.ChangeColor;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    private UserLocalDao userLocalDao;
    private String userID = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_main);

        ChangeColor.setStatusBarColor(this, Color.parseColor("#FFFFFFFF"), false);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userLocalDao = new UserLocalDao(getApplicationContext());
                userLocalDao.open();
                userID = userLocalDao.getPhoneNumber();
                Intent intent = new Intent();
                if (userID==null) {
                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                } else {
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                }
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 1000);


    }
}