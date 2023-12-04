package com.windsnow1025.health_management_app.fragment.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.api.GetInfoApi;
import com.windsnow1025.health_management_app.api.UpdateUsernameApi;
import com.windsnow1025.health_management_app.database.UserLocalDao;

public class SetNameFragment extends Fragment {

    private Button bt_back;
    private Button bt_set;
    private EditText et_name;
    private TextView tv_name;
    private UserLocalDao userLocalDao;
    private String phoneNumber;
    public SetNameFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    private void init(View view){
        tv_name=view.findViewById(R.id.tv_username);
        et_name=view.findViewById(R.id.et_username);
        bt_back=view.findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new BTlistener());
        bt_set=view.findViewById(R.id.bt_set);
        bt_set.setOnClickListener(new BTlistener());
    }
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_st_name, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        phoneNumber =userLocalDao.getPhoneNumber();
        init(view);
        tv_name.setText(userLocalDao.getUserInfo(phoneNumber).getUsername()+"");
        return view;
    }
    private class BTlistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.bt_back) {
                getParentFragmentManager().popBackStack();
            } else if (id == R.id.bt_set) {
                String newUsername=et_name.getText().toString();
                if(!newUsername.equals("")){
                    try {
                        UpdateUsernameApi updateUsernameApi = new UpdateUsernameApi();
                        updateUsernameApi.updateUsername(phoneNumber,newUsername);
                        GetInfoApi getInfoApi = new GetInfoApi();
                        userLocalDao.addOrUpdateUser(getInfoApi.getUserInformation(phoneNumber));
                        Toast.makeText(getContext(), "用户名称修改成功", Toast.LENGTH_SHORT).show();
                        tv_name.setText(newUsername);
                        et_name.setText("");
                        et_name.clearFocus();
                    }catch (Exception e){
                        Toast.makeText(getContext(), "用户名称修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }
}