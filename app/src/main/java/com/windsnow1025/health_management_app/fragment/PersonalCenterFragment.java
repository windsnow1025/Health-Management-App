package com.windsnow1025.health_management_app.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.windsnow1025.health_management_app.LoginActivity;
import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.api.GetInfoApi;
import com.windsnow1025.health_management_app.api.UpdateBirthdayApi;
import com.windsnow1025.health_management_app.pojo.UserInfo;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PersonalCenterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private Button bt_back;
    private Button bt_username;
    private Button bt_password;
    private Button bt_exit;
    private Button bt_age;


    private EditText et_password;

    private TextView tv_age;

    private int age;
    private Boolean flag = false;
    private FragmentTransaction transaction;
    private UserLocalDao userLocalDao;
    private String phoneNumber;
    private UserInfo userInfo;


    public PersonalCenterFragment() {
        // Required empty public constructor
    }

    public void init(View view) throws Exception {
        et_password = view.findViewById(R.id.et_password);
        bt_username = view.findViewById(R.id.bt_username);
        bt_password = view.findViewById(R.id.bt_password);
        bt_username.setOnClickListener(new btListener());
        bt_password.setOnClickListener(new btListener());
        bt_back = view.findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new btListener());
        bt_exit = view.findViewById(R.id.bt_exit);
        bt_exit.setOnClickListener(new btListener());
        tv_age = view.findViewById(R.id.tv_age);
        bt_age = view.findViewById(R.id.bt_age);
        phoneNumber = userLocalDao.getPhoneNumber();
        userInfo = userLocalDao.getUserInfo(phoneNumber);
        tv_age.setText(getAge(parse(userLocalDao.getUserInfo(phoneNumber).getBirthday())));
    }

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        try {
            init(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /* 重置年龄*/
        bt_age.setOnClickListener(new View.OnClickListener() {//设置监听器，打开日期控件
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(), PersonalCenterFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
            }
        });
        return view;
    }


    /*设置年龄信息*/
    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = year + "-" + (month + 1) + "-" + dayOfMonth;
        System.out.println(dateString);
        Date date2 = null;
        try {
            date2 = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (date.getTime() > date2.getTime()) {
            try {
                tv_age.setText(getAge(parse(year + "-" + (month + 1) + "-" + dayOfMonth)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String birthday=year + "-" + (month + 1) + "-" + dayOfMonth;
            UpdateBirthdayApi updateBirthdayApi = new UpdateBirthdayApi();
            updateBirthdayApi.updateBirthday(phoneNumber, birthday);
            GetInfoApi getInfoApi = new GetInfoApi();
            userLocalDao.addOrUpdateUser(getInfoApi.getUserInformation(phoneNumber));
        } else {
            Toast.makeText(getContext(), "出生日期不能小于当前日期", Toast.LENGTH_SHORT).show();
        }


    }

    private class btListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.bt_username) {
                transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SetNameFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            } else if (id == R.id.bt_password) {
                transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SetPasswordFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            } else if (id == R.id.bt_back) {
                getParentFragmentManager().popBackStack();
            } else if (id == R.id.bt_exit) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("退出登录？");
                builder.setMessage("请问您确定要退出当前登录吗？");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        userLocalDao.userLoginOut(phoneNumber);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        }
    }

    public static Date parse(String strDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(strDate);
    }

    public static String getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return String.valueOf(age);
    }
}