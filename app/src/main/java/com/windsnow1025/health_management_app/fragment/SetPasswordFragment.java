package com.windsnow1025.health_management_app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.api.UpdatePasswordApi;
import com.windsnow1025.health_management_app.sqlite.UserLocalDao;


public class SetPasswordFragment extends Fragment {

    private Button bt_back;
    private Button bt_set;
    private boolean flag = false;
    private ImageButton bt_eye;
    private EditText et_password1;
    private EditText et_password2;
    private TextView tv_password;
    private UserLocalDao userLocalDao;
    private String phoneNumber;

    public SetPasswordFragment() {
        // Required empty public constructor
    }

    private void init(View view){
        tv_password = view.findViewById(R.id.tv_password);
        et_password1 = view.findViewById(R.id.et_password1);
        et_password2 = view.findViewById(R.id.et_password2);
        bt_back = view.findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new BTlistener());
        bt_set = view.findViewById(R.id.bt_set);
        bt_set.setOnClickListener(new BTlistener());
        /*眼睛图标使用明密文*/
        bt_eye = view.findViewById(R.id.bt_eye);
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_st_pass, container, false);
        userLocalDao = new UserLocalDao(getActivity().getApplicationContext());
        userLocalDao.open();
        phoneNumber =userLocalDao.getPhoneNumber();
        init(view);
        bt_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = !flag;
                if (flag) {
                    /*明文*/
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.open_eye1));
                    tv_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    /*密文*/
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.eye));
                    tv_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });

        return view;
    }

    public class BTlistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.bt_back) {
                getParentFragmentManager().popBackStack();
            } else if (id == R.id.bt_set) {
                /*验证密码*/
                String newPassword = et_password1.getText().toString();
                String passwordRepeat = et_password2.getText().toString();
                if (newPassword.equals("") || passwordRepeat.equals("")) {
                    Toast.makeText(getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (newPassword.equals(passwordRepeat)) {
                    Toast.makeText(getContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                    tv_password.setText(newPassword);
                    et_password1.setText("");
                    et_password2.setText("");
                    et_password2.clearFocus();
                    UpdatePasswordApi updatePasswordApi = new UpdatePasswordApi();
                    updatePasswordApi.updatePassword(phoneNumber,newPassword);
                } else {
                    Toast.makeText(getContext(), "密码不正确，请再次确认", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}