package com.windsnow1025.health_management_app.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExistApi extends AsyncTask<String, Void, String> {

    private static final String API_URL_EXIST = "https://www.windsnow1025.com/learn/api/android/user/exist";

    @Override
    protected String doInBackground(String... params) {
        String phoneNumber = params[0];
        String apiResult = callApi(phoneNumber);
        return apiResult;
    }

    private String callApi(String phoneNumber) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(API_URL_EXIST);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // 构建请求体
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("phoneNumber", phoneNumber);

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
    public String checkUserUnique(String phoneNumber) {
        // 调用异步任务的 execute 方法
        execute(phoneNumber);
        try {
            JSONObject jsonResponse = new JSONObject(get());
            // 提取并返回API响应中的status
            return jsonResponse.getString("message");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
