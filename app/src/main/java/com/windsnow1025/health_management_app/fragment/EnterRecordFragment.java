package com.windsnow1025.health_management_app.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.jdbc.HistoryDao;
import com.windsnow1025.health_management_app.pojo.History;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;

import java.util.Calendar;

public class EnterRecordFragment extends Fragment {

    String organ;

    String username;

    String date;
    String hospital;
    String type;
    String symptom;
    String conclusion;
    String suggestion;

    EditText editTextDate;
    EditText editTextHospital;
    EditText editTextType;
    EditText editTextOrgan;
    EditText editTextSymptom;
    EditText editTextConclusion;
    EditText editTextSuggestion;

    public EnterRecordFragment(String organ) {
        this.organ = organ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_record, container, false);

        // Get username
        try {
            UserLocalDao userLocalDao = new UserLocalDao(getContext());
            userLocalDao.open();
            username = userLocalDao.getPhoneNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get views
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextHospital = view.findViewById(R.id.editTextHospital);
        editTextType = view.findViewById(R.id.editTextType);
        editTextOrgan = view.findViewById(R.id.editTextOrgan);
        editTextSymptom = view.findViewById(R.id.editTextSymptom);
        editTextConclusion = view.findViewById(R.id.editTextConclusion);
        editTextSuggestion = view.findViewById(R.id.editTextSuggestion);

        // Set default values
        editTextOrgan.setText(organ);

        // Set to open date picker when click on date EditText
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set date
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        // Set date to EditText
                        editTextDate.setText(date);
                    }
                }, year, month, day);

                // Show DatePickerDialog
                datePickerDialog.show();
            }
        });

        // Confirm button
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                date = editTextDate.getText().toString();
                hospital = editTextHospital.getText().toString();
                type = editTextType.getText().toString();
                organ = editTextOrgan.getText().toString();
                symptom = editTextSymptom.getText().toString();
                conclusion = editTextConclusion.getText().toString();
                suggestion = editTextSuggestion.getText().toString();

                // Insert data into database
                Boolean insertStatus = false;
                Log.i("主线程", "数据库测试开始");
                HistoryDao historyDao = new HistoryDao();
                History history = new History();
                history.setHistory_date(date);
                history.setHistory_place(hospital);
                history.setHistory_organ(type);
                history.setHistory_organ(organ);
                history.setSymptom(symptom);
                history.setConclusion(conclusion);
                history.setSuggestion(suggestion);
                history.setIs_deleted("false");
                try {
                    insertStatus = historyDao.insertHistory(username, history);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("主线程", "记录插入情况" + insertStatus);
                Log.i("主线程", "数据库测试结束");

                // Jump to organ page
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new OrganFragment(organ));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

}
