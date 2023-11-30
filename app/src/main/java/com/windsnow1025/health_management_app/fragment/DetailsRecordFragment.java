package com.windsnow1025.health_management_app.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.pojo.Alert;
import com.windsnow1025.health_management_app.pojo.Record;
import com.windsnow1025.health_management_app.pojo.Report;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;
import com.windsnow1025.health_management_app.utils.AlertAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetailsRecordFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private int i;
    private List<Alert> alertList;
    private Button rStartAlarm;
    private Button bt_rcancel;

    private EditText ret_title;
    private TextView ret_time;
    private TextView rtv_time;
    private TextView rtv_date;
    private TextView rtv_hospital;
    private TextView rtv_part;
    private EditText rtv_advice;
    AlertAdapter adapter;
    private boolean flag;
    private ArrayList<Report> reportArrayList;
    private ArrayList<Record> historyArrayList;
    private ArrayList<Alert> alertArrayList;
    private UserLocalDao userLocalDao;
    private Record record;
    private Report report;
    private Alert alert;
    private String userID;
    private int num, num_alerk;
    private boolean isreport;
    private boolean isMedicine;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private int H, M;

    /*用于新建*/
    public DetailsRecordFragment(boolean isMedicine, int n, boolean isReport, AlertAdapter infoAdapter) {
        this.num = n;
        this.isMedicine = isMedicine;
        this.adapter = infoAdapter;
        this.isreport = isReport;//是否为报告
    }

    /*用于修改*/
    public DetailsRecordFragment(boolean isMedicine, int n, AlertAdapter infoAdapter, List<Alert> AlertList, int I, boolean isReport, boolean flag) {
        this.adapter = infoAdapter;
        this.num = n;//num为捆绑的编号
        this.isMedicine = isMedicine;
        this.isreport = isReport;//是否为报告
        this.flag = flag;//是否为修改
        this.alertList = AlertList;
        this.i = I;//i为闹钟编号
    }

    private void init(View view) {
        rStartAlarm = view.findViewById(R.id.rStartAlarm);
        if (flag) {
            rStartAlarm.setText("修改");
        }
        rtv_time = view.findViewById(R.id.rtv_time);
        rtv_part = view.findViewById(R.id.rtv_part);
        rtv_date = view.findViewById(R.id.rtv_date);
        rtv_advice = view.findViewById(R.id.rtv_advice);
        ret_title = view.findViewById(R.id.ret_title);
        ret_time = view.findViewById(R.id.ret_time);
        bt_rcancel = view.findViewById(R.id.bt_cancel);
        rtv_hospital = view.findViewById(R.id.rtv_hospital);
        if (isreport) report = UserLocalDao.gerReport(reportArrayList, num);
        else record = UserLocalDao.getHistory(historyArrayList, num);
        num_alerk = userLocalDao.getAlertList(userID).size();
    }

    /*获取数据*/
    @SuppressLint("SetTextI18n")
    private void infoSet(boolean flag) {
        if (isreport) {
            rtv_time.setText(report.getReport_date() + "");
            rtv_part.setText(report.getReport_type() + "");
            rtv_advice.setText(report.getDetail() + "");
            rtv_hospital.setText(report.getHospital() + "");
        } else {
            rtv_time.setText(record.getRecord_date() + "");
            rtv_part.setText(record.getOrgan() + "");
            rtv_advice.setText(record.getSuggestion() + "");
            rtv_hospital.setText(record.getHospital() + "");
        }
        /*表修改状态，非新增时*/
        if (flag) {
            Alert alert1 = userLocalDao.getAlert(alertArrayList, i);
            ret_title.setText(alert1.getTitle());
            ret_time.setText(alert1.getAlert_date());
            String[] times = alert1.getAlert_date().split(":");
            H = Integer.parseInt(times[0]);
            M = Integer.parseInt(times[1]);
            rtv_date.setText(alert1.getAlert_cycle());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details__record, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        userID = userLocalDao.getPhoneNumber();
        alertArrayList = userLocalDao.getAlertList(userID);
        calendar = Calendar.getInstance();
        reportArrayList = userLocalDao.getReportList(userID);
        historyArrayList = userLocalDao.getRecordList(userID);
        init(view);
        infoSet(flag);

        rtv_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(), DetailsRecordFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            dialog.show();
        });
        ret_time.setOnClickListener(v -> {

            timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view1, int hourOfDay, int minute) {
                    H = hourOfDay;
                    M = minute;
                    String str = hourOfDay + ":" + minute;
                    ret_time.setText(str);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
        rStartAlarm.setOnClickListener(v -> {
            if (!ret_title.getText().toString().equals("") && !rtv_date.getText().toString().equals("") && !ret_time.getText().toString().equals("")) {
                Alert alert = new Alert(num_alerk, userID, "提醒", rtv_advice.getText().toString(), ret_title.getText().toString(), ret_time.getText().toString(), rtv_date.getText().toString(), isMedicine + "");
                DetailsRecordFragment.this.alert = new Alert(num_alerk, userID, "提醒", rtv_advice.getText().toString(), ret_title.getText().toString(), ret_time.getText().toString(), rtv_date.getText().toString(), isMedicine + "");
                // 是否为修改
                if (flag) {
                    DetailsRecordFragment.this.alert = new Alert(i, userID, "提醒", rtv_advice.getText().toString(), ret_title.getText().toString(), ret_time.getText().toString(), rtv_date.getText().toString(), isMedicine + "");
                    userLocalDao.updateAlert(userID, DetailsRecordFragment.this.alert);
                } else {
                    userLocalDao.insertAlert(userID, DetailsRecordFragment.this.alert);
                }
                adapter.add(alert);
                if (!flag) {
                    Toast.makeText(getContext(), "提醒添加成功", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getContext(), "提醒修改成功", Toast.LENGTH_SHORT).show();
                setAlarm(H, M);
                requireActivity().getSupportFragmentManager().popBackStack();
            } else Toast.makeText(getContext(), "信息不完整", Toast.LENGTH_SHORT).show();
        });
        bt_rcancel.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("取消修改？");
            builder.setMessage("取消编辑将返回原界面，本次修改将不被保存！");
            builder.setNegativeButton("继续编辑", null);
            builder.setPositiveButton("确定返回", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Fragment fragment = new AlertFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            builder.show();
        });


        return view;
    }


    private void setAlarm(int hour, int minute) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, ret_title.getText().toString());
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        ArrayList<Integer> days = new ArrayList<>();
        days.add(Calendar.SUNDAY);
        days.add(Calendar.MONDAY);
        days.add(Calendar.TUESDAY);
        days.add(Calendar.WEDNESDAY);
        days.add(Calendar.THURSDAY);
        days.add(Calendar.FRIDAY);
        days.add(Calendar.SATURDAY);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        if (date.getTime() < date2.getTime()) {
            month += 1;
            rtv_date.setText(year + "-" + month + "-" + dayOfMonth);
        } else {
            Toast.makeText(getContext(), "提醒日期不能早于当前日期", Toast.LENGTH_SHORT).show();
        }


    }
}