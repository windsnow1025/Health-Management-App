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

public class UpdateReportApi extends AsyncTask<ArrayList<Report>, Void, String> {

    private static final String API_URL_SYNC_UPDATE_REPORT = "https://www.windsnow1025.com/learn/api/android/sync/update/report";

    @Override
    protected String doInBackground(ArrayList<Report>... params) {
        ArrayList<Report> reports = params[0];

        // 先调用上传API将报告信息发送到服务器
        String apiResult = callApi(reports);
        return apiResult;
    }

    private String callApi(ArrayList<Report> reports) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(API_URL_SYNC_UPDATE_REPORT);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // 构建请求体
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("data", convertReportsToJsonArray(reports));
            jsonParam.put("phone_number", reports.get(0).getPhone_number());

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

    private JSONArray convertReportsToJsonArray(ArrayList<Report> reports) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Report report : reports) {
            JSONObject jsonReport = new JSONObject();
            jsonReport.put("phone_number", report.getPhone_number());
            jsonReport.put("id", report.getId());
            jsonReport.put("detail", report.getDetail());
            jsonReport.put("picture", report.getPicture());
            jsonReport.put("report_type", report.getReport_type());
            jsonReport.put("hospital", report.getHospital());
            jsonReport.put("report_date", report.getReport_date());

            jsonArray.put(jsonReport);
        }

        return jsonArray;
    }

    public String updateReports(ArrayList<Report> reports) {
        execute(reports);
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
            Log.d("API Response", "UploadReportApi Raw Response: " + result);
        } else {
            Log.e("API Response", "UploadReportApi 上传报告信息时发生异常或为空");
        }
    }
}

