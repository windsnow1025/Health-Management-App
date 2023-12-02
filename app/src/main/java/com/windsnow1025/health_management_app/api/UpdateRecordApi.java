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

public class UpdateRecordApi extends AsyncTask<ArrayList<Record>, Void, String> {

    private static final String API_URL_SYNC_UPDATE_RECORD = "https://www.windsnow1025.com/learn/api/android/sync/update/record";

    @Override
    protected String doInBackground(ArrayList<Record>... params) {
        ArrayList<Record> records = params[0];

        // 先调用上传API将记录信息发送到服务器
        String apiResult = callApi(records);
        return apiResult;
    }

    private String callApi(ArrayList<Record> records) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(API_URL_SYNC_UPDATE_RECORD);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // 构建请求体
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("data", convertRecordsToJsonArray(records));
            jsonParam.put("phone_number", records.get(0).getPhone_number());

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

    private JSONArray convertRecordsToJsonArray(ArrayList<Record> records) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Record record : records) {
            JSONObject jsonRecord = new JSONObject();
            jsonRecord.put("phone_number", record.getPhone_number());
            jsonRecord.put("id", record.getId());
            jsonRecord.put("record_date", record.getRecord_date());
            jsonRecord.put("hospital", record.getHospital());
            jsonRecord.put("doctor", record.getDoctor());
            jsonRecord.put("organ", record.getOrgan());
            jsonRecord.put("symptom", record.getSymptom());
            jsonRecord.put("conclusion", record.getConclusion());
            jsonRecord.put("suggestion", record.getSuggestion());

            jsonArray.put(jsonRecord);
        }

        return jsonArray;
    }

    public String updateRecords(ArrayList<Record> records) {
        execute(records);
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
            Log.d("API Response", "UpdateRecordApi Raw Response: " + result);
        } else {
            Log.e("API Response", "UpdateRecordApi 上传记录信息时发生异常或为空");
        }
    }
}

