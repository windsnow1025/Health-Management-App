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
import com.windsnow1025.health_management_app.jdbc.HistoryDao;
import com.windsnow1025.health_management_app.pojo.Record;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
            String username = userLocalDao.getPhoneNumber();

            // Get history list
            HistoryDao historyDao = new HistoryDao();
            ArrayList<Record> histories;
            try {
                histories=historyDao.getHistoryList(username);
                Log.i("test","从服务器获取就诊记录");
            }
            catch (TimeoutException e)
            {
                Log.i("test","超时，从本地获取就诊记录");
                histories = userLocalDao.getRecordList(username);
            }

            // Set history list to recycler view
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"时间", "医院", "部位"});
            for (Record history : histories) {
                data.add(new String[]{history.getHistory_date(), history.getHistory_place(), history.getHistory_organ()});
            }

            RecyclerView recyclerView = view.findViewById(R.id.record_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ArrayList<Record> finalHistories = histories;
            recyclerView.setAdapter(new TableEnterAdapter(data, new TableEnterAdapter.OnItemClickListener() {
                // Edit Button
                @Override
                public void onClick(int position) {
                    // Get record id
                    Integer record_id = finalHistories.get(position - 1).getHistory_No();

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new EditRecordFragment(record_id));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }, new TableEnterAdapter.OnItemClickListener() {
                // Delete Button
                @Override
                public void onClick(int position) {
                    // Get record id
                    Integer record_id = finalHistories.get(position - 1).getHistory_No();

                    // Delete record
                    try {
                        historyDao.deleteHistory(username, record_id);
                        Log.i("test","从服务器删除就诊记录");
                    }
                    catch (TimeoutException e)
                    {
                        Log.i("test","超时，从本地删除就诊记录");
                        userLocalDao.deleteRecord(username, record_id);
                    }

                    // Reload fragment
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new OrganFragment(organ));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button buttonEnterRecord = view.findViewById(R.id.buttonEnterRecord);
        buttonEnterRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new EnterRecordFragment(organ));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

}
