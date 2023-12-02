package com.windsnow1025.health_management_app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.TableAdapter;
import com.windsnow1025.health_management_app.pojo.Record;
import com.windsnow1025.health_management_app.pojo.Report;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;
import com.windsnow1025.health_management_app.utils.AlertAdapter;

import java.util.ArrayList;
import java.util.List;


public class SetAlertFragment extends Fragment {

    private View view;
    private TableAdapter tableAdapter1;
    private TableAdapter tableAdapter2;
    private  ArrayList<Report> reportArrayList;
    private  ArrayList<Record> historyArrayList;
    private UserLocalDao userLocalDao;
    private String userID;
    AlertAdapter adapter;
    ListView listView;
    public SetAlertFragment(AlertAdapter madapter, ListView mlistView) {
        this.adapter=madapter;
        this.listView=mlistView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_alert_set, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        userID=userLocalDao.getPhoneNumber();
        reportArrayList=userLocalDao.getReportList(userID);
        historyArrayList=userLocalDao.getRecordList(userID);

        List<String[]> data = new ArrayList<>();
        for (Report report:reportArrayList
             ) {//时间、地点、类型、编号
            data.add(new String[]{report.getReport_date(), report.getHospital(), report.getReport_type(), String.valueOf(report.getId())});
        }

        List<String[]> data1 = new ArrayList<>();
        for (Record history :
                historyArrayList) {
            data1.add(new String[]{history.getRecord_date(),history.getHospital(),history.getOrgan(), String.valueOf(history.getId())});
        }



        RecyclerView recyclerView = view.findViewById(R.id.record_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tableAdapter1=new TableAdapter(data,new TableAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("类型选择");
                builder.setMessage("请选择为该记录添加提醒的类型");
                builder.setNegativeButton("吃药提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        int i= Integer.parseInt(data.get(pos)[3]);
                        transaction.replace(R.id.fragment_container, new AlertMedicineFragment(true,i,true,adapter));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                builder.setPositiveButton("复诊提醒", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        int i= Integer.parseInt(data.get(pos)[3]);
                        transaction.replace(R.id.fragment_container, new AlertDiagnoseFragment(false,i,true,adapter));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                builder.show();
            }
        });

        recyclerView.setAdapter(tableAdapter1);


        RecyclerView recyclerView1 = view.findViewById(R.id.recycler_view1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        tableAdapter2=new TableAdapter(data1,new TableAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("类型选择");
                builder.setMessage("请选择为该记录添加提醒的类型");
                builder.setNegativeButton("吃药提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        int i= Integer.parseInt(data1.get(pos)[3]);
                        transaction.replace(R.id.fragment_container, new AlertMedicineFragment(true,i,false,adapter));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                builder.setPositiveButton("复诊提醒", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        int i= Integer.parseInt(data1.get(pos)[3]);
                        transaction.replace(R.id.fragment_container, new AlertDiagnoseFragment(false,i,false,adapter));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                builder.show();

            }

        });
        recyclerView1.setAdapter(tableAdapter2);


        return view;

    }


}