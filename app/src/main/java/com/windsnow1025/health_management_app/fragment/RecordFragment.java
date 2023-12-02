package com.windsnow1025.health_management_app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.TableEnterAdapter;
import com.windsnow1025.health_management_app.pojo.Record;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;

import java.util.ArrayList;
import java.util.List;

// 就诊记录显示
public class RecordFragment extends Fragment {

    View view;
    String organ;

    public RecordFragment(String organ) {
        this.organ = organ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, container, false);

        // Get history list from database
        try {
            // Get username
            UserLocalDao userLocalDao = new UserLocalDao(getContext());
            userLocalDao.open();
            String phoneNumber = userLocalDao.getPhoneNumber();

            // Get record list
            ArrayList<Record> records;
            records = userLocalDao.getRecordList(phoneNumber);
            Log.i("test", "从服务器获取就诊记录");

            // Set history list to recycler view
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"时间", "医院", "部位"});
            for (Record record : records) {
                data.add(new String[]{record.getRecord_date(), record.getHospital(), record.getOrgan()});
            }

            RecyclerView recyclerView = view.findViewById(R.id.record_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ArrayList<Record> finalRecords = records;
            // Delete Button
            // Edit Button
            recyclerView.setAdapter(new TableEnterAdapter(data, position -> {
                // Get record id
                Integer record_id = finalRecords.get(position - 1).getId();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new EditRecordFragment(record_id));
                transaction.addToBackStack(null);
                transaction.commit();
            }, position -> {
                // Get record id
                int record_id = finalRecords.get(position - 1).getId();

                // Delete record
                userLocalDao.deleteRecord(phoneNumber, record_id);
                Log.i("test", "从服务器删除就诊记录");

                // Reload fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new OrganFragment(organ));
                transaction.addToBackStack(null);
                transaction.commit();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button buttonEnterRecord = view.findViewById(R.id.buttonEnterRecord);
        buttonEnterRecord.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new EnterRecordFragment(organ));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

}
