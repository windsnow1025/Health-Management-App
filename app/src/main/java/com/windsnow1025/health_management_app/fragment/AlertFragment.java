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
import com.windsnow1025.health_management_app.jdbc.AlertDao;
import com.windsnow1025.health_management_app.pojo.Alert;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;
import com.windsnow1025.health_management_app.utils.Info;
import com.windsnow1025.health_management_app.utils.InfoAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class AlertFragment extends Fragment {
    private static List<Info> infoList = new ArrayList<>();
    private static List<Integer> numList = new ArrayList<>();
    private InfoAdapter adapter;
    private ListView listView;
    private TableLayout tableLayout;
    private Button button;
    private boolean report, flag,medicine;
    private UserLocalDao userLocalDao;
    private AlertDao alertDao;
    private ArrayList<Alert> alertArrayList;
    private String userID, s,ismedicine;

    private void init(View view) {
        listView = view.findViewById(R.id.list_view);
        tableLayout = view.findViewById(R.id.tableLayout);
        button = view.findViewById(R.id.btn_add_dada);
    }

    /*读取数据*/
    public List<Info> getInfoList() {
        infoList.clear();
        numList.clear();
        alertArrayList = userLocalDao.getAlertList(userID);
        for (Alert alert : alertArrayList
        ) {
            s = alert.getType();
            ismedicine=alert.getIs_medicine();
            medicine=ismedicine.equals("true");//吃药否
            flag = s.equals("true");//true为体检报告
            numList.add(alert.getAlert_No());
            infoList.add(new Info(medicine,alert.getContent(), alert.getDate(), alert.getCycle(), flag, alert.getType_No(), alert.getAlert_No()));
        }

        return infoList;
    }
    void load() {
        try {
            alertArrayList = alertDao.getAlertList(userID);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        int x = userLocalDao.getAlertList(userID).size();
        if (x > 0) {
            for (int i = x; i >= 0; i--) {
                userLocalDao.deleteAlert(userID, i);
            }
        }
        for (Alert alert : alertArrayList) {
            userLocalDao.insertAlert(userID, alert);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        userID = userLocalDao.getPhoneNumber();
        alertDao = new AlertDao();
        load();
        infoList = getInfoList();
        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        init(view);

        if (infoList.isEmpty()) {
            tableLayout.setVisibility(View.GONE);
            view.findViewById(R.id.tv_blank).setVisibility(View.VISIBLE);
        } else {
            tableLayout.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_blank).setVisibility(View.GONE);
        }

        adapter = new InfoAdapter(requireContext(), R.layout.listview, infoList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                report = infoList.get(position).getFlag();//是否为报告
                boolean is_drug = infoList.get(position).getMedicine();//是否吃药
                int num = infoList.get(position).getN();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                if (is_drug) {
                    if (report) {
                        transaction.replace(R.id.fragment_container, new DetailsFragment(is_drug, num, adapter, infoList, numList.get(position), report, true));
                    } else {
                        transaction.replace(R.id.fragment_container, new DetailsFragment(is_drug, num, adapter, infoList, numList.get(position), report, true));

                    }
                } else {
                    if (report) {
                        transaction.replace(R.id.fragment_container, new DetailsRecordFragment(is_drug, num, adapter, infoList, numList.get(position), report, true));
                    } else {
                        transaction.replace(R.id.fragment_container, new DetailsRecordFragment(is_drug, num, adapter, infoList, numList.get(position), report, true));

                    }

                }
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("删除该提醒？");
                builder.setMessage("请问您确定要删除该提醒吗？");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        infoList.remove(position);
                        try {
                            alertDao.deleteAlert(userID, numList.get(position));
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        userLocalDao.deleteAlert(userID, numList.get(position));
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
            }
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