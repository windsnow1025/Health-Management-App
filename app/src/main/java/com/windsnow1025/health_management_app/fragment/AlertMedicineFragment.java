package com.windsnow1025.health_management_app.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.pojo.Alert;
import com.windsnow1025.health_management_app.pojo.Record;
import com.windsnow1025.health_management_app.pojo.Report;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;
import com.windsnow1025.health_management_app.utils.AlertAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AlertMedicineFragment extends Fragment {

    private final ArrayList<Integer> Time = new ArrayList<>();
    private CheckBox Monday;
    private CheckBox Tuesday;
    private CheckBox Wednesday;
    private CheckBox Thursday;
    private CheckBox Friday;
    private CheckBox Saturday;
    private CheckBox Sunday;

    private Button StartAlarm;
    private Button bt_cancel;

    private EditText et_title;
    private TextView et_time;
    private TextView tv_time;
    private TextView tv_hospital;
    private TextView tv_part;
    private TextView tv_advice;
    private int H, M, alert_id;
    AlertAdapter adapter;
    private boolean flag = false;
    private ArrayList<Report> reportArrayList;
    private ArrayList<Record> recordArrayList;
    private ArrayList<Alert> alertArrayList;
    private UserLocalDao userLocalDao;
    private Record record;
    private Report report;
    private Alert alert;
    private String phoneNumber;
    private int bindID;
    private int num_alert;
    private boolean is_report;
    private boolean isMedicine;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;


    /*用于新建*/
    public AlertMedicineFragment(boolean isMedicine, int bindID, boolean is_report, AlertAdapter infoAdapter) {
        this.bindID = bindID;
        this.adapter = infoAdapter;
        this.isMedicine = isMedicine;
        this.is_report = is_report;
    }

    /*用于修改*/
    public AlertMedicineFragment(boolean isMedicine, int bindID, AlertAdapter infoAdapter, List<Alert> AlertList,  int alert_id,boolean is_report, boolean Flag) {
        this.adapter = infoAdapter;
        this.isMedicine = isMedicine;
        this.bindID = bindID;
        this.is_report = is_report;
        this.alert_id = alert_id;
        this.flag = Flag;//是否为修改
    }

    private void init(View view) {
        Monday = view.findViewById(R.id.Monday);
        Tuesday = view.findViewById(R.id.Tuesday);
        Wednesday = view.findViewById(R.id.Wednesday);
        Thursday = view.findViewById(R.id.Thursday);
        Friday = view.findViewById(R.id.Friday);
        Saturday = view.findViewById(R.id.Saturday);
        Sunday = view.findViewById(R.id.Sunday);
        StartAlarm = view.findViewById(R.id.StartAlarm);
        if (flag) {
            StartAlarm.setText("修改");
        }
        tv_time = view.findViewById(R.id.tv_time);
        tv_part = view.findViewById(R.id.tv_part);
        tv_hospital = view.findViewById(R.id.tv_hospital);
        tv_advice = view.findViewById(R.id.tv_advice);
        et_title = view.findViewById(R.id.et_title);
        et_time = view.findViewById(R.id.et_time);
        bt_cancel = view.findViewById(R.id.bt_cancel);

        if (is_report) {
            report = UserLocalDao.getReport(reportArrayList, bindID);
        }
        else {
            record = UserLocalDao.getRecord(recordArrayList, bindID);
        }
        num_alert = userLocalDao.getAlertList(phoneNumber).size();
    }

    /*数据导入*/
    @SuppressLint("SetTextI18n")
    private void infoSet(boolean flag) {
        if (is_report) {
            tv_time.setText(report.getReport_date());
            tv_part.setText(report.getReport_type());
            tv_advice.setText(report.getDetail());
            tv_hospital.setText(report.getHospital());
        } else {
            tv_time.setText(record.getRecord_date());
            tv_part.setText(record.getOrgan());
            tv_advice.setText(record.getSuggestion());
            tv_hospital.setText(record.getHospital());
        }

        /*表修改状态，非新增时*/
        if (flag) {
            Alert alert1 = userLocalDao.getAlert(alertArrayList, alert_id);
            et_title.setText(alert1.getTitle());
            et_time.setText(alert1.getAlert_date());
            String[] times = alert1.getAlert_date().split(":");
            H = Integer.parseInt(times[0]);
            M = Integer.parseInt(times[1]);
            String[] newArray = alert1.getAlert_cycle().split("\\s");
            for (String str : newArray) {
                switch (str) {
                    case "周日" -> Sunday.setChecked(true);
                    case "周一" -> Monday.setChecked(true);
                    case "周二" -> Tuesday.setChecked(true);
                    case "周三" -> Wednesday.setChecked(true);
                    case "周四" -> Thursday.setChecked(true);
                    case "周五" -> Friday.setChecked(true);
                    case "周六" -> Saturday.setChecked(true);
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        phoneNumber = userLocalDao.getPhoneNumber();
        alertArrayList = userLocalDao.getAlertList(phoneNumber);
        calendar = Calendar.getInstance();
        reportArrayList = userLocalDao.getReportList(phoneNumber);
        recordArrayList = userLocalDao.getRecordList(phoneNumber);
        init(view);
        infoSet(flag);
        et_time.setOnClickListener(v -> {

            timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                H = hourOfDay;
                M = minute;
                String str = hourOfDay + ":" + minute;
                et_time.setText(str);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
        /*创建闹钟*/
        StartAlarm.setOnClickListener(v -> {
            setAlarmTime();
            if (!et_title.getText().toString().equals("") && !Time.isEmpty() && !et_time.getText().toString().equals("")) {
                Alert alert;
                if (is_report) {
                    String alert_type = report.getReport_type();
                    alert = new Alert(num_alert, 0, bindID, phoneNumber, alert_type, tv_advice.getText().toString(), et_title.getText().toString(), et_time.getText().toString(), getDate(Time), isMedicine + "");
                } else {
                    String alert_type = record.getOrgan();
                    alert = new Alert(num_alert, bindID, 0, phoneNumber, alert_type, tv_advice.getText().toString(), et_title.getText().toString(), et_time.getText().toString(), getDate(Time), isMedicine + "");
                }
                AlertMedicineFragment.this.alert = alert;
                // 是否为修改
                if (flag) {
                    userLocalDao.deleteAlert(phoneNumber,alert_id);
                    userLocalDao.insertAlert(phoneNumber, AlertMedicineFragment.this.alert);
                } else {
                    userLocalDao.insertAlert(phoneNumber, AlertMedicineFragment.this.alert);
                }
                adapter.add(alert);
                if (!flag) {
                    Toast.makeText(getContext(), "提醒添加成功", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getContext(), "提醒修改成功", Toast.LENGTH_SHORT).show();
                setAlarm(H, M);
                requireActivity().getSupportFragmentManager().popBackStack();
            } else Toast.makeText(getContext(), "信息不完整", Toast.LENGTH_SHORT).show();
        });
        bt_cancel.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("取消修改？");
            builder.setMessage("取消编辑将返回原界面，本次修改将不被保存！");
            builder.setNegativeButton("继续编辑", null);
            builder.setPositiveButton("确定返回", (dialog, which) -> {
                Fragment fragment = new AlertFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
            builder.show();
        });


        return view;
    }

    static String getDate(ArrayList<Integer> Time) {

        StringBuilder str = new StringBuilder();
        for (Integer i : Time) {
            switch (i) {
                case 1:
                    str.append("周天 ");
                    break;
                case 2:
                    str.append("周一 ");
                    break;
                case 3:
                    str.append("周二 ");
                    break;
                case 4:
                    str.append("周三 ");
                    break;
                case 5:
                    str.append("周四 ");
                    break;
                case 6:
                    str.append("周五 ");
                    break;
                case 7:
                    str.append("周六 ");
                    break;
            }
        }

        return str.toString();
    }


    private void setAlarmTime() {
        Time.clear();
        if (Monday.isChecked())
            Time.add(Calendar.MONDAY);
        if (Tuesday.isChecked())
            Time.add(Calendar.TUESDAY);
        if (Wednesday.isChecked())
            Time.add(Calendar.WEDNESDAY);
        if (Thursday.isChecked())
            Time.add(Calendar.THURSDAY);
        if (Friday.isChecked())
            Time.add(Calendar.FRIDAY);
        if (Saturday.isChecked())
            Time.add(Calendar.SATURDAY);
        if (Sunday.isChecked())
            Time.add(Calendar.SUNDAY);
    }

    private void setAlarm(int hour, int minute) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, et_title.getText().toString());
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        ArrayList<Integer> days = new ArrayList<>();
        if (Sunday.isChecked()) days.add(Calendar.SUNDAY);
        if (Monday.isChecked()) days.add(Calendar.MONDAY);
        if (Tuesday.isChecked()) days.add(Calendar.TUESDAY);
        if (Wednesday.isChecked()) days.add(Calendar.WEDNESDAY);
        if (Thursday.isChecked()) days.add(Calendar.THURSDAY);
        if (Friday.isChecked()) days.add(Calendar.FRIDAY);
        if (Saturday.isChecked()) days.add(Calendar.SATURDAY);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}