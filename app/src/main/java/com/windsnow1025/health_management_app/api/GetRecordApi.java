package com.windsnow1025.health_management_app.api;

import android.os.AsyncTask;
import android.util.Log;

import com.windsnow1025.health_management_app.pojo.Record;

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

public class GetRecordApi extends AsyncTask<String, Void, String> {

    private static final String API_URL_SYNC_GET_RECORD = "https://www.windsnow1025.com/learn/api/android/sync/get/record";

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
            URL url = new URL(API_URL_SYNC_GET_RECORD);
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

    // 用于获取Record信息
    public List<Record> getRecordInformation(String phoneNumber) {
        // 调用异步任务的 execute 方法，将用户名作为参数传递
        execute(phoneNumber);
        try {
            // 获取异步任务的结果，即API响应
            String apiResult = get();

            List<Record> records = new ArrayList<>();
            try {
                // 从API响应中提取用户信息
                JSONArray jsonArray = new JSONArray(apiResult);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonResponse = jsonArray.getJSONObject(i);

                    Record record = new Record();
                    record.setPhone_number(jsonResponse.getString("phone_number"));
                    record.setId(jsonResponse.getInt("id"));
                    record.setRecord_date(jsonResponse.getString("record_date"));
                    record.setHospital(jsonResponse.getString("hospital"));
                    record.setDoctor(jsonResponse.getString("doctor"));
                    record.setOrgan(jsonResponse.getString("organ"));
                    record.setSymptom(jsonResponse.getString("symptom"));
                    record.setConclusion(jsonResponse.getString("conclusion"));
                    record.setSuggestion(jsonResponse.getString("suggestion"));

                    records.add(record);
                }

                return records;
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException("GetRecordApi 解析用户信息时发生异常: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GetRecordApi 获取用户信息时发生异常: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // 在UI线程中处理返回的结果
        if (result != null) {
            Log.d("API Response", "GetRecordApi Raw Response: " + result);
        } else {
            Log.e("API Response", "GetRecordApi 获取手机号时发生异常或为空");
        }
    }
}
