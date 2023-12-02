package com.windsnow1025.health_management_app.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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


public class DetailsFragment extends Fragment {

    private final ArrayList<Integer> Time = new ArrayList<Integer>();
    private List<Alert> alertList;
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
    private int H, M, i;
    AlertAdapter adapter;
    private boolean flag = false;
    private ArrayList<Report> reportArrayList;
    private ArrayList<Record> historyArrayList;
    private ArrayList<Alert> alertArrayList;
    private UserLocalDao userLocalDao;
    private Record history;
    private Report report;
    private Alert alert;
    private String phoneNumber;
    private int num, num_alerk;
    private boolean isReport;
    private boolean isMedicine;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;


    /*用于新建*/
    public DetailsFragment(boolean isMedicine, int n, boolean isReport, AlertAdapter infoAdapter) {
        this.num = n;//num为捆绑的编号
        this.adapter = infoAdapter;
        this.isMedicine = isMedicine;
        this.isReport = isReport;//是否为报告
    }

    /*用于修改*/
    public DetailsFragment(boolean isMedicine, int n, AlertAdapter infoAdapter, List<Alert> AlertList, int I, boolean isReport, boolean Flag) {
        this.adapter = infoAdapter;
        this.isMedicine = isMedicine;
        this.num = n;//num为捆绑的编号
        this.isReport = isReport;//是否为报告
        this.flag = Flag;//是否为修改
        this.alertList = AlertList;
        this.i = I;//i为闹钟编号
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

        if (isReport) report = UserLocalDao.getReport(reportArrayList, num);
        else {
            history = UserLocalDao.getHistory(historyArrayList, num);
        }
        num_alerk = userLocalDao.getAlertList(phoneNumber).size();
    }

    /*数据导入*/
    @SuppressLint("SetTextI18n")
    private void infoSet(boolean flag) {
        if (isReport) {
            tv_time.setText(report.getReport_date() + "");
            tv_part.setText(report.getReport_type() + "");
            tv_advice.setText(report.getDetail() + "");
            tv_hospital.setText(report.getHospital() + "");
        } else {
            tv_time.setText(history.getRecord_date() + "");
            tv_part.setText(history.getOrgan() + "");
            tv_advice.setText(history.getSuggestion() + "");
            tv_hospital.setText(history.getHospital() + "");
        }

        /*表修改状态，非新增时*/
        if (flag) {
            Alert alert1 = userLocalDao.getAlert(alertArrayList, i);
            et_title.setText(alert1.getTitle());
            et_time.setText(alert1.getAlert_date());
            String[] times = alert1.getAlert_date().split(":");
            H = Integer.parseInt(times[0]);
            M = Integer.parseInt(times[1]);
            String[] newArray = alert1.getAlert_cycle().split("\\s");
            for (String str : newArray) {
                switch (str) {
                    case "周日":
                        Sunday.setChecked(true);
                        break;
                    case "周一":
                        Monday.setChecked(true);
                        break;
                    case "周二":
                        Tuesday.setChecked(true);
                        break;
                    case "周三":
                        Wednesday.setChecked(true);
                        break;
                    case "周四":
                        Thursday.setChecked(true);
                        break;
                    case "周五":
                        Friday.setChecked(true);
                        break;
                    case "周六":
                        Saturday.setChecked(true);
                        break;
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        phoneNumber = userLocalDao.getPhoneNumber();
        alertArrayList = userLocalDao.getAlertList(phoneNumber);
        calendar = Calendar.getInstance();
        reportArrayList = userLocalDao.getReportList(phoneNumber);
        historyArrayList = userLocalDao.getRecordList(phoneNumber);
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
                Alert alert = new Alert(num_alerk, phoneNumber, "用药提醒", tv_advice.getText().toString(), et_title.getText().toString(), et_time.getText().toString(), getDate(Time), isMedicine + "");
                DetailsFragment.this.alert = new Alert(num_alerk, phoneNumber, "用药提醒", tv_advice.getText().toString(), et_title.getText().toString(), et_time.getText().toString(), getDate(Time), isMedicine + "");
                System.out.println("编号" + num_alerk);

//                   是否为修改
                if (flag) {
                    DetailsFragment.this.alert = new Alert(i, phoneNumber, "用药提醒", tv_advice.getText().toString(), et_title.getText().toString(), et_time.getText().toString(), getDate(Time), isMedicine + "");
                    System.out.println("编号" + i);
                    userLocalDao.updateAlert(phoneNumber, DetailsFragment.this.alert);

                } else {
                    userLocalDao.insertAlert(phoneNumber, DetailsFragment.this.alert);
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