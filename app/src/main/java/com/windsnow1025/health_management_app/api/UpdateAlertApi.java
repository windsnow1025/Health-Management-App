package com.windsnow1025.health_management_app.api;

import android.os.AsyncTask;
import android.util.Log;

import com.windsnow1025.health_management_app.pojo.Alert;

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

public class UpdateAlertApi extends AsyncTask<ArrayList<Alert>, Void, String> {

    private static final String API_URL_SYNC_UPDATE_ALERT = "https://www.windsnow1025.com/learn/api/android/sync/update/alert";

    @Override
    protected String doInBackground(ArrayList<Alert>... params) {
        ArrayList<Alert> alerts = params[0];

        // 先调用上传API将提醒信息发送到服务器
        String apiResult = callApi(alerts);
        return apiResult;
    }

    private String callApi(ArrayList<Alert> alerts) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(API_URL_SYNC_UPDATE_ALERT);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // 构建请求体
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("data", convertAlertsToJsonArray(alerts));
            jsonParam.put("phone_number", alerts.get(0).getPhone_number());

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

    private JSONArray convertAlertsToJsonArray(ArrayList<Alert> alerts) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Alert alert : alerts) {
            JSONObject jsonAlert = new JSONObject();
            jsonAlert.put("phone_number", alert.getPhone_number());
            jsonAlert.put("ID", alert.getID());
            jsonAlert.put("alert_type", alert.getAlert_type());
            jsonAlert.put("advice", alert.getAdvice());
            jsonAlert.put("title", alert.getTitle());
            jsonAlert.put("alert_date", alert.getAlert_date());
            jsonAlert.put("alert_cycle", alert.getAlert_cycle());
            jsonAlert.put("is_medicine", alert.getIs_medicine());

            jsonArray.put(jsonAlert);
        }

        return jsonArray;
    }

    public String updateAlerts(ArrayList<Alert> alerts) {
        execute(alerts);
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
            Log.d("API Response", "UpdateAlertApi Raw Response: " + result);
        } else {
            Log.e("API Response", "UpdateAlertApi 上传提醒信息时发生异常或为空");
        }
    }
}
