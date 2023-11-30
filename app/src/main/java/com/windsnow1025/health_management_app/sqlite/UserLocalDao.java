package com.windsnow1025.health_management_app.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.windsnow1025.health_management_app.pojo.Alert;
import com.windsnow1025.health_management_app.pojo.Record;
import com.windsnow1025.health_management_app.pojo.Report;
import com.windsnow1025.health_management_app.pojo.User;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserLocalDao {
    private Context context;
    private SqliteHelper dbHelper;
    private SQLiteDatabase db;
    private ReportDao reportDao;
    private RecordDao recordDao;
    private AlertDao alertDao;

    public UserLocalDao(Context context) {
        this.context = context;
        reportDao = new ReportDao();
        recordDao = new RecordDao();
        alertDao = new AlertDao();
    }

    public UserLocalDao() {
        reportDao = new ReportDao();
        recordDao = new RecordDao();
        alertDao = new AlertDao();
    }

    public void open() throws SQLiteException {
        dbHelper = new SqliteHelper(context);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException exception) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    //获取当前登录账号
    @SuppressLint("Range")
    public String getPhoneNumber() {
        String phoneNumber = null;
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                {
                    phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
                }
            } while (cursor.moveToNext());
        }
        return phoneNumber;
    }

    public Boolean checkUser(String phoneNumber) {
        Cursor cursor = db.query("user", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    @SuppressLint("Range")
    public User getUserInfo(String phoneNumber) {
        User userInfo = new User();
        Cursor cursor = db.query("user", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToFirst()) {
            userInfo.setPhone_number(phoneNumber);
            userInfo.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            userInfo.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
            userInfo.setSex(cursor.getString(cursor.getColumnIndex("sex")));
        }
        return userInfo;
    }

    public void addOrUpdateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("phone_number", user.getPhone_number());
        values.put("username", user.getUsername());
        values.put("birthday", user.getBirthday());
        values.put("sex", user.getSex());
        if (checkUser(user.getPhone_number())) {
            db.update("user", values, "phone_number=?", new String[]{user.getPhone_number()});
        } else {
            db.insert("user", null, values);
        }
    }

    //账号登出
    public void userLoginOut(String phoneNumber) {
        db.delete("user", "phone_number = ?", new String[]{phoneNumber});
    }

    @SuppressLint("Range")
    public ArrayList<Record> getRecordList(String phoneNumber) {
        ArrayList<Record> historyArrayList = new ArrayList<>();
        Cursor cursor = db.query("record", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setPhone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
                record.setRecord_date(cursor.getString(cursor.getColumnIndex("record_date")));
                record.setHospital(cursor.getString(cursor.getColumnIndex("hospital")));
                record.setDoctor(cursor.getString(cursor.getColumnIndex("doctor")));
                record.setOrgan(cursor.getString(cursor.getColumnIndex("organ")));
                record.setConclusion(cursor.getString(cursor.getColumnIndex("conclusion")));
                record.setSymptom(cursor.getString(cursor.getColumnIndex("symptom")));
                record.setSuggestion(cursor.getString(cursor.getColumnIndex("suggestion")));
                historyArrayList.add(record);
            } while (cursor.moveToNext());
        }
        return historyArrayList;
    }

    public Boolean insertRecord(String phoneNumber, Record record) {
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("ID", record.getID());
        values.put("record_date", record.getRecord_date());
        values.put("hospital", record.getHospital());
        values.put("doctor", record.getDoctor());
        values.put("organ", record.getOrgan());
        values.put("conclusion", record.getConclusion());
        values.put("symptom", record.getSymptom());
        values.put("suggestion", record.getSuggestion());
        long rowsAffected = db.insert("record", null, values);
        return rowsAffected > 0;
    }

//    private Integer getHistoryCount(String account) {
//        Integer valueReturn=0;
//        valueReturn=db.query("history", null, "phone_number = ?", new String[]{account}, null, null, null).getCount();
//        return valueReturn+1;
//    }

    public Boolean updateRecord(String phoneNumber, Record record) {
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("ID", record.getID());
        values.put("record_date", record.getRecord_date());
        values.put("hospital", record.getHospital());
        values.put("doctor", record.getDoctor());
        values.put("organ", record.getOrgan());
        values.put("conclusion", record.getConclusion());
        values.put("symptom", record.getSymptom());
        values.put("suggestion", record.getSuggestion());
        int rowsAffected = db.update("history", values, "ID = ? AND phone_number=?", new String[]{String.valueOf(record.getID()), phoneNumber});
        return rowsAffected > 0;
    }

    public Boolean deleteRecord(String phoneNumber, int ID) {
        int rowsAffected = db.delete("history", "ID = ? AND phone_number = ?", new String[]{String.valueOf(ID), phoneNumber});
        return rowsAffected > 0;
    }


    @SuppressLint("Range")
    public ArrayList<Report> getReportList(String phoneNumber) {
        ArrayList<Report> reportArrayList = new ArrayList<>();
        Cursor cursor = db.query("report", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setPhone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
                report.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                report.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                report.setPicture(cursor.getBlob(cursor.getColumnIndex("picture")));
                report.setReport_type(cursor.getString(cursor.getColumnIndex("report_type")));
                report.setHospital(cursor.getString(cursor.getColumnIndex("hospital")));
                report.setReport_date(cursor.getString(cursor.getColumnIndex("report_date")));
                reportArrayList.add(report);
            } while (cursor.moveToNext());
        }
        return reportArrayList;
    }

    public Boolean insertReport(String phoneNumber, Report report) {
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("ID", report.getID());
        values.put("detail", report.getDetail());
        values.put("picture", report.getPicture().toString());
        values.put("report_type", report.getReport_type());
        values.put("hospital", report.getHospital());
        values.put("report_date", report.getReport_date());
        long rowsAffected = db.insert("report", null, values);
        return rowsAffected > 0;
    }

//    private Integer getReportCount(String account) {
//        Integer valueReturn=0;
//        valueReturn=db.query("report", null, "phone_number = ?", new String[]{account}, null, null, null).getCount();
//        return valueReturn+1;
//    }

    public Boolean updateReport(String phoneNumber, Report report) {
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("ID", report.getID());
        values.put("detail", report.getDetail());
        values.put("report_picture", report.getPicture().toString());
        values.put("report_type", report.getReport_type());
        values.put("hospital", report.getHospital());
        values.put("report_date", report.getReport_date());
        int rowsAffected = db.update("report", values, "ID = ? AND phone_number=?", new String[]{String.valueOf(report.getID()), phoneNumber});
        return rowsAffected > 0;
    }

    public Boolean deleteReport(String phoneNumber, int ID) {
        int flag = db.delete("report", "ID = ? AND phone_number=?", new String[]{String.valueOf(ID), phoneNumber});
        return flag > 0;
    }

    @SuppressLint("Range")
    public ArrayList<Alert> getAlertList(String phoneNumber) {
        ArrayList<Alert> alertArrayList = new ArrayList<>();
        Cursor cursor = db.query("alert", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Alert alert = new Alert();
                alert.setPhone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
                alert.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                alert.setAlert_date(cursor.getString(cursor.getColumnIndex("alert_date")));
                alert.setAdvice(cursor.getString(cursor.getColumnIndex("advice")));
                alert.setAlert_cycle(cursor.getString(cursor.getColumnIndex("alert_cycle")));
                alert.setAlert_type(cursor.getString(cursor.getColumnIndex("alert_type")));
                alert.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                alert.setIs_medicine(cursor.getString(cursor.getColumnIndex("is_medicine")));
                alertArrayList.add(alert);
            } while (cursor.moveToNext());
        }
        return alertArrayList;
    }

    public Boolean insertAlert(String phoneNumber, Alert alert) {
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("ID", alert.getID());
        values.put("alert_date", alert.getAlert_date());
        values.put("alert_cycle", alert.getAlert_cycle());
        values.put("advice", alert.getAdvice());
        values.put("alert_type", alert.getAlert_type());
        values.put("title", alert.getTitle());
        values.put("is_medicine", alert.getIs_medicine());
        long rowsAffected = db.insert("alert", null, values);
        return rowsAffected > 0;
    }

//    private Integer getAlertCount(String account) {
//        Integer valueReturn=0;
//        valueReturn=db.query("alert", null, "phone_number = ?", new String[]{account}, null, null, null).getCount();
//        return valueReturn+1;
//    }

    public Boolean updateAlert(String phoneNumber, Alert alert) {
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("ID", alert.getID());
        values.put("alert_date", alert.getAlert_date());
        values.put("alert_cycle", alert.getAlert_cycle());
        values.put("advice", alert.getAdvice());
        values.put("alert_type", alert.getAlert_type());
        values.put("title", alert.getTitle());
        values.put("is_medicine", alert.getIs_medicine());
        int rowsAffected = db.update("alert", values, "ID=? AND phone_number=?", new String[]{String.valueOf(alert.getID()), phoneNumber});
        return rowsAffected > 0;
    }

    public Boolean deleteAlert(String account, Integer alert_No) {
        int flag = db.delete("alert","Alert_No=? AND phone_number=?", new String[]{String.valueOf(alert_No), account});
        return flag > 0;
    }

//    public Boolean deleteAlert(String account,Integer alert_No){
//        Boolean valueReturn=false;
//        int flag=db.delete("alert","Alert_No=? AND phone_number=?",new String[]{String.valueOf(alert_No),account});
//        if(flag>0)
//        {
//            valueReturn=true;
//        }
//        return valueReturn;
//    }

    public static Alert getAlert(ArrayList<Alert> alertArrayList, Integer alert_No) {
        Stream<Alert> alertStream = alertArrayList.stream();
        Alert alert = alertStream.filter(e -> e.getID() == alert_No).collect(Collectors.toList()).get(0);
        return alert;
    }

    public static Record getHistory(ArrayList<Record> historyArrayList, Integer history_No) {
        Stream<Record> historyStream = historyArrayList.stream();
        Record record = historyStream.filter(e -> e.getID() == history_No).collect(Collectors.toList()).get(0);
        return record;
    }

    public static Report gerReport(ArrayList<Report> reportArrayList, Integer report_No) {
        Stream<Report> reportStream = reportArrayList.stream();
        Report report = reportStream.filter(e -> e.getID() == report_No).collect(Collectors.toList()).get(0);
        return report;
    }
}
