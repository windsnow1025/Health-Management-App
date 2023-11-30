package com.windsnow1025.health_management_app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.pojo.Alert;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;
import com.windsnow1025.health_management_app.utils.AlertAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlertFragment extends Fragment {
    private static List<Alert> alertList = new ArrayList<>();
    private static List<Integer> numList = new ArrayList<>();
    private AlertAdapter adapter;
    private ListView listView;
    private TableLayout tableLayout;
    private Button button;
    private boolean is_report, alert_type_bool, isMedicine_bool;
    private UserLocalDao userLocalDao;
    private ArrayList<Alert> alertArrayList;
    private String phoneNumber, alert_type, isMedicine;

    private void init(View view) {
        listView = view.findViewById(R.id.list_view);
        tableLayout = view.findViewById(R.id.tableLayout);
        button = view.findViewById(R.id.btn_add_dada);
    }

    /*读取数据*/
    public List<Alert> getAlertList() {
        alertList.clear();
        numList.clear();
        alertArrayList = userLocalDao.getAlertList(phoneNumber);
        for (Alert alert : alertArrayList
        ) {
            alert_type = alert.getAlert_type();
            isMedicine = alert.getIs_medicine();
            isMedicine_bool = isMedicine.equals("true");//吃药否
            alert_type_bool = alert_type.equals("true");//true为体检报告
            numList.add(alert.getID());
            alertList.add(new Alert(alert.getID(), alert.getPhone_number(), alert.getAlert_type(), alert.getAdvice(), alert.getTitle(), alert.getAlert_date(), alert.getAlert_cycle(), alert.getIs_medicine()));
        }

        return alertList;
    }

    void load() {
        alertArrayList = userLocalDao.getAlertList(phoneNumber);
        int size = userLocalDao.getAlertList(phoneNumber).size();
        if (size > 0) {
            for (int i = size; i >= 0; i--) {
                userLocalDao.deleteAlert(phoneNumber, i);
            }
        }
        for (Alert alert : alertArrayList) {
            userLocalDao.insertAlert(phoneNumber, alert);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        phoneNumber = userLocalDao.getPhoneNumber();
        load();
        alertList = getAlertList();
        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        init(view);

        if (alertList.isEmpty()) {
            tableLayout.setVisibility(View.GONE);
            view.findViewById(R.id.tv_blank).setVisibility(View.VISIBLE);
        } else {
            tableLayout.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_blank).setVisibility(View.GONE);
        }

        adapter = new AlertAdapter(requireContext(), R.layout.listview, alertList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view12, position, id) -> {
            is_report = alertList.get(position).getAlert_type().equals("true");//是否为报告
            Boolean isMedicine = alertList.get(position).getIs_medicine().equals("true");//是否为吃药
            int ID = alertList.get(position).getID();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            if (isMedicine) {
                if (is_report) {
                    transaction.replace(R.id.fragment_container, new DetailsFragment(isMedicine, ID, adapter, alertList, numList.get(position), is_report, true));
                } else {
                    transaction.replace(R.id.fragment_container, new DetailsFragment(isMedicine, ID, adapter, alertList, numList.get(position), is_report, true));

                }
            } else {
                if (is_report) {
                    transaction.replace(R.id.fragment_container, new DetailsRecordFragment(isMedicine, ID, adapter, alertList, numList.get(position), is_report, true));
                } else {
                    transaction.replace(R.id.fragment_container, new DetailsRecordFragment(isMedicine, ID, adapter, alertList, numList.get(position), is_report, true));

                }

            }
            transaction.addToBackStack(null);
            transaction.commit();
        });
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("删除该提醒？");
            builder.setMessage("请问您确定要删除该提醒吗？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertList.remove(position);
                    userLocalDao.deleteAlert(phoneNumber, numList.get(position));
                    userLocalDao.deleteAlert(phoneNumber, numList.get(position));
                    Fragment fragment = new AlertFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    Toast.makeText(getContext(), "提醒删除成功", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
            return false;
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SetAlertFragment(adapter, listView));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
}