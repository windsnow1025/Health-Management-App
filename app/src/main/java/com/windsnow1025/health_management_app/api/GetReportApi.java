package com.windsnow1025.health_management_app.api;

import android.os.AsyncTask;
import android.util.Log;

import com.windsnow1025.health_management_app.model.Report;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetReportApi extends AsyncTask<String, Void, String> {

    private static final String API_URL_SYNC_GET_REPORT = "https://www.windsnow1025.com/learn/api/android/sync/get/report";


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
            URL url = new URL(API_URL_SYNC_GET_REPORT);
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

    // 用于获取Report信息
    public List<Report> getReportInformation(String phoneNumber) {
        // 调用异步任务的 execute 方法，将用户名作为参数传递
        execute(phoneNumber);
        try {
            // 获取异步任务的结果，即API响应
            String apiResult = get();

            List<Report> reports = new ArrayList<>();
            try {
                // 从API响应中提取用户信息
                JSONArray jsonArray = new JSONArray(apiResult);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonResponse = jsonArray.getJSONObject(i);

                    Report report = new Report();
                    report.setPhone_number(jsonResponse.getString("phone_number"));
                    report.setId(jsonResponse.getInt("id"));
                    report.setReport_date(jsonResponse.getString("report_date"));
                    report.setHospital(jsonResponse.getString("hospital"));
                    report.setReport_type(jsonResponse.getString("report_type"));
                    report.setPicture(jsonResponse.getString("picture"));
                    report.setDetail(jsonResponse.getString("detail"));

                    reports.add(report);
                }

                return reports;
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException("GetReportApi 解析用户信息时发生异常: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GetReportApi 获取用户信息时发生异常: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // 在UI线程中处理返回的结果
        if (result != null) {
            Log.d("API Response", "GetReportApi Raw Response: " + result);
        } else {
            Log.e("API Response", "GetReportApi 获取手机号时发生异常或为空");
        }
    }
}
