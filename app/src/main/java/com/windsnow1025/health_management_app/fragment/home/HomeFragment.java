package com.windsnow1025.health_management_app.fragment.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.LoginActivity;
import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.api.GetAlertApi;
import com.windsnow1025.health_management_app.api.GetRecordApi;
import com.windsnow1025.health_management_app.api.GetReportApi;
import com.windsnow1025.health_management_app.api.UpdateAlertApi;
import com.windsnow1025.health_management_app.api.UpdateRecordApi;
import com.windsnow1025.health_management_app.api.UpdateReportApi;
import com.windsnow1025.health_management_app.model.Alert;
import com.windsnow1025.health_management_app.database.UserLocalDao;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class HomeFragment extends Fragment {
    private ImageButton imageButton;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton_get;
    private ImageButton bt_disease;
    private ImageButton bt_kefu;
    private TextView tv_user;
    private String phoneNumber;

    private UserLocalDao userLocalDao;
    private ArrayList<Alert> alertArrayList;

    public HomeFragment() {

    }

    void init(View view) throws TimeoutException {
        tv_user = view.findViewById(R.id.textViewLoginSignup);
        imageButton = view.findViewById(R.id.imageButton);
        imageButton1 = view.findViewById(R.id.imageButton1);
        imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton3 = view.findViewById(R.id.imageButton3);
        imageButton_get = view.findViewById(R.id.imageButton_get);
        bt_disease = view.findViewById(R.id.bt_disease);
        bt_kefu = view.findViewById(R.id.bt_kefu);
        phoneNumber = userLocalDao.getPhoneNumber();
        userLocalDao.addOrUpdateUser(userLocalDao.getUserInfo(phoneNumber));
    }

    void load() {
        UpdateRecordApi updateRecordApi = new UpdateRecordApi();
        updateRecordApi.updateRecords(userLocalDao.getRecordList(phoneNumber));
        UpdateReportApi updateReportApi = new UpdateReportApi();
        updateReportApi.updateReports(userLocalDao.getReportList(phoneNumber));
        UpdateAlertApi updateAlertApi = new UpdateAlertApi();
        updateAlertApi.updateAlerts(userLocalDao.getAlertList(phoneNumber));
    }


    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        try {
            init(view);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        /*若用户已登录则让按钮失效*/
        if (phoneNumber != null) {
            tv_user.setEnabled(false);
            tv_user.setText(phoneNumber);
        } else tv_user.setText("请先登录!");
        tv_user.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });


        /*个人中心*/
        imageButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new UserCenterFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });


        /*数据上传*/
        imageButton1.setOnClickListener(v -> {
            load();
            Toast.makeText(getContext(), "数据已实时上传成功", Toast.LENGTH_SHORT).show();

        });

        /*数据下载*/
        imageButton_get.setOnClickListener(v -> {
            userLocalDao.deleteAlerts(phoneNumber);
            userLocalDao.deleteRecords(phoneNumber);
            userLocalDao.deleteReports(phoneNumber);
            GetReportApi getReportApi = new GetReportApi();
            userLocalDao.insertReports(phoneNumber,getReportApi.getReportInformation(phoneNumber));
            GetRecordApi getRecordApi = new GetRecordApi();
            userLocalDao.insertRecords(phoneNumber,getRecordApi.getRecordInformation(phoneNumber));
            GetAlertApi getAlertApi = new GetAlertApi();
            userLocalDao.insertAlerts(phoneNumber,getAlertApi.getAlertInformation(phoneNumber));
            Toast.makeText(getContext(), "数据已实时下载成功", Toast.LENGTH_SHORT).show();

        });


        /*切换账号*/
        imageButton2.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("切换用户？");
            builder.setMessage("请问您确定要退出当前登录吗？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", (dialog, which) -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                userLocalDao.userLoginOut(phoneNumber);
                startActivity(intent);
            });
            builder.show();

        });

        /*设置*/
        imageButton3.setOnClickListener(v -> {
            FragmentTransaction transaction3 = getParentFragmentManager().beginTransaction();
            transaction3.replace(R.id.fragment_container, new SettingFragment());
            transaction3.addToBackStack(null);
            transaction3.commit();
        });

        /*常见疾病*/
        bt_disease.setOnClickListener(v -> {
            FragmentTransaction transaction3 = getParentFragmentManager().beginTransaction();
            transaction3.replace(R.id.fragment_container, new DiseaseWebFragment());
            transaction3.addToBackStack(null);
            transaction3.commit();
        });

        /*联系我们*/
        bt_kefu.setOnClickListener(v -> {
            FragmentTransaction transaction3 = getParentFragmentManager().beginTransaction();
            transaction3.replace(R.id.fragment_container, new CommonFragment(4));
            transaction3.addToBackStack(null);
            transaction3.commit();
        });
        return view;
    }


}
