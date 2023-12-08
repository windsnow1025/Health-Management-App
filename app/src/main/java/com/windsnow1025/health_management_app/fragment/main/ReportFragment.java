package com.windsnow1025.health_management_app.fragment.main;

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
import com.windsnow1025.health_management_app.database.UserLocalDao;
import com.windsnow1025.health_management_app.model.Report;

import java.util.ArrayList;
import java.util.List;

// 体检报告显示
public class ReportFragment extends Fragment {

    View view;
    String organ;

    public ReportFragment(String organ) {
        this.organ = organ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);

        // Get report list from database
        try {
            // Get username
            UserLocalDao userLocalDao = new UserLocalDao(getContext());
            userLocalDao.open();
            String phoneNumber = userLocalDao.getPhoneNumber();

            // Get report list
            ArrayList<Report> reports;
            reports = userLocalDao.getReportList(phoneNumber);

            // Set report list to recycler view
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"时间", "医院", "类型"});
            for (Report report : reports) {
                data.add(new String[]{report.getReport_date(), report.getHospital(), report.getReport_type()});
            }

            RecyclerView recyclerView = view.findViewById(R.id.report_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ArrayList<Report> finalReports = reports;

            recyclerView.setAdapter(new TableEnterAdapter(data, position -> {
                // Edit report
                int report_id = finalReports.get(position - 1).getId();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new EditReportFragment(report_id, organ));
                transaction.addToBackStack(null);
                transaction.commit();
            }, position -> {
                // Delete report
                int report_id = finalReports.get(position - 1).getId();

                userLocalDao.deleteReport(phoneNumber, report_id);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainOrganFragment(organ));
                transaction.addToBackStack(null);
                transaction.commit();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button buttonEnterReport = view.findViewById(R.id.buttonEnterReport);
        buttonEnterReport.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new EnterReportFragment(organ));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
