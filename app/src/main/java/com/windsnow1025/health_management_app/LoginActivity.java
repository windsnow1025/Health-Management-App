package com.windsnow1025.health_management_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.windsnow1025.health_management_app.api.GetAlertApi;
import com.windsnow1025.health_management_app.api.GetRecordApi;
import com.windsnow1025.health_management_app.api.GetReportApi;
import com.windsnow1025.health_management_app.api.GetInfoApi;
import com.windsnow1025.health_management_app.api.SigninApi;
import com.windsnow1025.health_management_app.database.UserLocalDao;
import com.windsnow1025.health_management_app.utils.ViewUtil;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText et_phone;
    private EditText et_password;
    private CheckBox ck_remember;
    private Button btn_forget;
    private Button btn_register;
    private Button btn_login;
    private ImageButton bt_eye;
    private boolean bRemember = false;
    private boolean flag = false;
    private String phoneNumber;
    private String password;
    private Intent intent;
    private SharedPreferences mShared;
    private Boolean flag_eye = false;
    private UserLocalDao userLocalDao;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLocalDao = new UserLocalDao(getApplicationContext());
        userLocalDao.open();
        setContentView(R.layout.activity_login);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        ck_remember = (CheckBox) findViewById(R.id.ck_remember);
        ck_remember.setOnCheckedChangeListener(new CheckListener());
        btn_forget = (Button) findViewById(R.id.btn_forget);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        ck_remember.setOnCheckedChangeListener(new CheckListener());
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_forget.setOnClickListener(this);
        et_password.setOnFocusChangeListener(this);
        et_phone.addTextChangedListener(new HideText(et_phone, et_password));
        bt_eye = findViewById(R.id.bt_eye1);
        bt_eye.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                flag_eye = !flag_eye;
                if (flag_eye) {
                    /*明文*/
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.open_eye1));
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    /*密文*/
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.eye));
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        /*保留手机号码*/
        intent = getIntent();
        phoneNumber = intent.getStringExtra("username");
        if (phoneNumber != null) {
            et_phone.setText(phoneNumber);
        }
        /*记住密码功能*/
        mShared = getSharedPreferences("share_login", MODE_PRIVATE);
        String phone = mShared.getString("phone", "");
        String password = mShared.getString("password", "");
        if (!phone.equals("")) {
            et_phone.setText(phone);
        }
        if (!password.equals("")) {
            et_password.setText(password);
        }

    }


    @Override
    public void onClick(View v) {

        phoneNumber = et_phone.getText().toString();
        password = et_password.getText().toString();
        /*注册*/
        if (v.getId() == R.id.btn_register) {
            intent = new Intent(this, LoginForgetActivity.class);
            intent.putExtra("username", phoneNumber);
            intent.putExtra("flag", true);
            startActivity(intent);
        }
        /*登录*/
        else if (v.getId() == R.id.btn_login) {
            intent = new Intent(this, MainActivity.class);
            flag = true;
            boolean is= false;
            try {
                SigninApi signinApi = new SigninApi();
                is = phoneNumber.equals(signinApi.checkUserPassword(phoneNumber, password));
            } catch (RuntimeException e) {
            }
            if (is) {
                if (bRemember) {
                    SharedPreferences.Editor editor = mShared.edit();
                    editor.putString("phone", et_phone.getText().toString());
                    editor.putString("password", et_password.getText().toString());
                    editor.apply();
                    GetInfoApi getInfoApi = new GetInfoApi();
                    userLocalDao.addOrUpdateUser(getInfoApi.getUserInformation(phoneNumber));
                    userLocalDao.deleteAlerts(phoneNumber);
                    userLocalDao.deleteRecords(phoneNumber);
                    userLocalDao.deleteReports(phoneNumber);
                    GetReportApi getReportApi = new GetReportApi();
                    userLocalDao.insertReports(phoneNumber,getReportApi.getReportInformation(phoneNumber));
                    GetRecordApi getRecordApi = new GetRecordApi();
                    userLocalDao.insertRecords(phoneNumber,getRecordApi.getRecordInformation(phoneNumber));
                    GetAlertApi getAlertApi = new GetAlertApi();
                    userLocalDao.insertAlerts(phoneNumber,getAlertApi.getAlertInformation(phoneNumber));

                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = mShared.edit();
                    editor.putString("phone", "");
                    editor.putString("password", "");
                    editor.apply();
                    GetInfoApi getInfoApi = new GetInfoApi();
                    userLocalDao.addOrUpdateUser(getInfoApi.getUserInformation(phoneNumber));
                    userLocalDao.deleteAlerts(phoneNumber);
                    userLocalDao.deleteRecords(phoneNumber);
                    userLocalDao.deleteReports(phoneNumber);
                    GetReportApi getReportApi = new GetReportApi();
                    userLocalDao.insertReports(phoneNumber,getReportApi.getReportInformation(phoneNumber));
                    GetRecordApi getRecordApi = new GetRecordApi();
                    userLocalDao.insertRecords(phoneNumber,getRecordApi.getRecordInformation(phoneNumber));
                    GetAlertApi getAlertApi = new GetAlertApi();
                    userLocalDao.insertAlerts(phoneNumber,getAlertApi.getAlertInformation(phoneNumber));

                    startActivity(intent);
                }
            } else Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
        /*忘记密码*/
        else if (v.getId() == R.id.btn_forget) {
            intent = new Intent(this, LoginForgetActivity.class);
            intent.putExtra("username", phoneNumber);
            intent.putExtra("flag", false);
            startActivity(intent);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            String phone = et_phone.getText().toString();
            if (TextUtils.isEmpty(phone) || phone.length() < 11) {
                et_phone.requestFocus();
                Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*是否勾选“记住我”按钮*/
    private class CheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.ck_remember) {
                bRemember = isChecked;
            }
        }
    }

    //从修改密码页面返回登录页面，要清空密码的输入框
    @Override
    protected void onRestart() {
        et_password.setText("");
        super.onRestart();
    }

    /*监听、隐藏软键盘*/
    private class HideText extends Activity implements TextWatcher {

        private EditText v;
        private View nextview;
        private CharSequence str;
        private int maxlength;

        public HideText(EditText editText, View vnext) {
            super();
            v = editText;
            nextview = vnext;
            maxlength = ViewUtil.getMaxLength(editText);
        }


        /*返回当前页面的activity*/


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            str = s;
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (str == null || str.length() == 0) {
                return;
            }
            if (str.length() == 11 && maxlength == 11) {
                ViewUtil.hideMethod(LoginActivity.this, v);
                nextview.requestFocus();
            }
            if (str.length() == 6 && maxlength == 6) {
                ViewUtil.hideMethod(LoginActivity.this, v);
                nextview.requestFocus();
            }
        }


    }
}