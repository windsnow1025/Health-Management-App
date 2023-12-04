package com.windsnow1025.health_management_app.api;


import android.os.AsyncTask;
import android.util.Log;

import com.windsnow1025.health_management_app.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SigninApi extends AsyncTask<String, Void, String> {

    private static final String API_URL_SIGNIN = "https://www.windsnow1025.com/learn/api/android/user/signin";

    @Override
    protected String doInBackground(String... params) {
        String phoneNumber = params[0];
        String password = params[1];

        // 先调用登录API获取用户信息
        String apiResult = callSignInApi(phoneNumber, password);
        return apiResult;
    }

    private String callSignInApi(String phoneNumber, String password) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(API_URL_SIGNIN);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // 构建请求体
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("phoneNumber", phoneNumber);
            jsonParam.put("password", password);

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(jsonParam.toString().getBytes("UTF-8"));
            outputStream.close();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // 返回响应字符串
                return response.toString();
            } else {
                return "HTTP请求失败，响应码: " + responseCode;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "发生异常: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    // 用于检查用户密码并返回手机号
    public String checkUserPassword(String phoneNumber, String password) {
        // 调用异步任务的 execute 方法，将用户名和密码作为参数传递
        execute(phoneNumber, password);
        try {
            JSONObject jsonResponse = new JSONObject(get());
            // 提取并返回API响应中的手机号
            return jsonResponse.getString("phoneNumber");
            // 获取异步任务的结果，即手机号
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 或者返回空字符串，取决于你的需求
        }
    }

    // 用于获取用户信息
    public User getUserInformation(String phoneNumber, String password) {
        // 调用异步任务的 execute 方法，将用户名作为参数传递
        execute(phoneNumber,password);
        try {
            // 获取异步任务的结果，即API响应
            String apiResult = get();

            User userInfo = new User();
            try {
                // 从API响应中提取用户信息
                JSONObject jsonResponse = new JSONObject(apiResult);

                userInfo.setPhone_number(jsonResponse.getString("phoneNumber"));
                userInfo.setUsername(jsonResponse.getString("username"));
                userInfo.setSex(jsonResponse.getString("sex"));
                userInfo.setBirthday(jsonResponse.getString("birthday"));

                return userInfo;
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException("SigninApi 解析用户信息时发生异常: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SigninApi 获取用户信息时发生异常: " + e.getMessage());
        }
    }



    @Override
    protected void onPostExecute(String result) {
        // 在UI线程中处理返回的结果
        if (result != null) {
            Log.d("API Response", "SigninApi Raw Response: " + result);
        } else {
            Log.e("API Response", "SigninApi 获取手机号时发生异常或为空");
        }
    }
}
