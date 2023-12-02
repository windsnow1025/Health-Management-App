package com.windsnow1025.health_management_app.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private AlertAdapter adapter;
    private ListView listView;
    private TableLayout tableLayout;
    private Button button;
    private boolean is_report;
    private UserLocalDao userLocalDao;
    private ArrayList<Alert> alertArrayList;
    private String phoneNumber;

    private void init(View view) {
        listView = view.findViewById(R.id.list_view);
        tableLayout = view.findViewById(R.id.tableLayout);
        button = view.findViewById(R.id.btn_add_dada);
    }

    /*读取数据*/
    public List<Alert> getAlertList() {
        alertList.clear();
        alertArrayList = userLocalDao.getAlertList(phoneNumber);

        return alertArrayList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        phoneNumber = userLocalDao.getPhoneNumber();
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
            Alert alert = alertList.get(position);
            int bind_id;
            if (alert.getReport_id() > 0) {
                is_report = true;
                bind_id = alert.getReport_id();
            } else {
                is_report = false;
                bind_id = alert.getRecord_id();
            }
            boolean isMedicine = alertList.get(position).getIs_medicine().equals("true");//是否为吃药

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            if (isMedicine) {
                transaction.replace(R.id.fragment_container, new AlertMedicineFragment(isMedicine, bind_id, adapter, alertList, alertList.get(position).getId(), is_report, true));
            } else {
                transaction.replace(R.id.fragment_container, new AlertDiagnoseFragment(isMedicine, bind_id, adapter, alertList, alertList.get(position).getId(), is_report, true));
            }
            transaction.addToBackStack(null);
            transaction.commit();
        });
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("删除该提醒？");
            builder.setMessage("请问您确定要删除该提醒吗？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", (dialog, which) -> {
                if( position < alertList.size()) {
                    int alertId = alertList.get(position).getId();
                    userLocalDao.deleteAlert(phoneNumber, alertId);
                    alertList.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "提醒删除成功", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
            return true; // 如果不希望其他监听器响应此事件，返回 true
        });

        button.setOnClickListener(view13 -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SetAlertFragment(adapter, listView));
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }
}