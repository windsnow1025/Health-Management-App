package com.windsnow1025.health_management_app.api;

import android.os.AsyncTask;
import android.util.Log;

import com.windsnow1025.health_management_app.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetInfoApi extends AsyncTask<String, Void, String> {

    private static final String API_URL_GET = "https://www.windsnow1025.com/learn/api/android/user/info";

    @Override
    protected String doInBackground(String... params) {
        String phoneNumber = params[0];


        // 先调用登录API获取用户信息
        String apiResult = callApi(phoneNumber);
        return apiResult;
    }

    private String callApi(String phoneNumber) {
        HttpURLConnection urlConnection = null;

        try {
            // 构建带有查询参数的URL
            URL url = new URL(API_URL_GET + "?phoneNumber=" + phoneNumber);
            urlConnection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为GET
            urlConnection.setRequestMethod("GET");

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
        } catch (IOException e) {
            e.printStackTrace();
            return "发生异常: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    // 用于检查用户密码并返回手机号
    public User getUserInformation(String phoneNumber) {
        // 调用异步任务的 execute 方法，将用户名作为参数传递
        execute(phoneNumber);
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
                throw new RuntimeException("解析用户信息时发生异常: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取用户信息时发生异常: " + e.getMessage());
        }
    }


    @Override
    protected void onPostExecute(String result) {
        // 在UI线程中处理返回的结果
        if (result != null) {
            Log.d("API Response", "Raw Response: " + result);
        } else {
            Log.e("API Response", "发生异常或为空");
        }
    }
}
