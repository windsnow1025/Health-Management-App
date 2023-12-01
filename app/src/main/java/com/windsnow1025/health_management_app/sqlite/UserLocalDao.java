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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserLocalDao {
    private Context context;
    private SqliteHelper dbHelper;
    private SQLiteDatabase db;

    public UserLocalDao(Context context) {
        this.context = context;
    }

    public UserLocalDao() {
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
    public Boolean checkReport(String phoneNumber) {
        Cursor cursor = db.query("report", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }
    public Boolean checkRecord(String phoneNumber) {
        Cursor cursor = db.query("record", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }    public Boolean checkAlert(String phoneNumber) {
        Cursor cursor = db.query("alert", null, "phone_number = ?", new String[]{phoneNumber}, null, null, null);
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

    //仿照上面的addOrUpdateUser写个addOrUpdateReport
    public void addOrUpdateReport(Report report){
        ContentValues values = new ContentValues();
        values.put("phone_number",report.getPhone_number());
        values.put("report_date", report.getReport_date());
        values.put("hospital", report.getHospital());
        values.put("report_type", report.getReport_type());
        values.put("picture", report.getPicture());
        values.put("detail", report.getDetail());
        if (checkReport(report.getPhone_number())) {
            db.update("report", values, "phone_number=?", new String[]{report.getPhone_number()});
        } else {
            db.insert("report", null, values);
        }
    }
    //仿照上面的addOrUpdateUser写个addOrUpdateRecord
    public void addOrUpdateRecord(Record record){
        ContentValues values = new ContentValues();
        values.put("phone_number",record.getPhone_number());
        values.put("report_date", record.getRecord_date());
        values.put("hospital", record.getHospital());
        values.put("doctor", record.getDoctor());
        values.put("organ", record.getOrgan());
        values.put("symptom", record.getSymptom());
        values.put("conclusion", record.getConclusion());
        values.put("suggestion", record.getSuggestion());
        if (checkRecord(record.getPhone_number())) {
            db.update("record", values, "phone_number=?", new String[]{record.getPhone_number()});
        } else {
            db.insert("record", null, values);
        }
    }

    public Boolean insertOrUpdateReport(String phoneNumber, Report report) {
        // 检查数据库中是否存在指定的记录
        Cursor cursor = db.query("report", null, "ID = ? AND phone_number=?", new String[]{String.valueOf(report.getID()), phoneNumber}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            // 数据库中存在记录，执行更新操作
            ContentValues values = new ContentValues();
            values.put("phone_number", phoneNumber);
            values.put("ID", report.getID());
            values.put("detail", report.getDetail());
            values.put("report_picture", report.getPicture().toString());
            values.put("report_type", report.getReport_type());
            values.put("hospital", report.getHospital());
            values.put("report_date", report.getReport_date());
            int rowsAffected = db.update("report", values, "ID = ? AND phone_number=?", new String[]{String.valueOf(report.getID()), phoneNumber});
            cursor.close();
            return rowsAffected > 0;
        } else {
            // 数据库中不存在记录，执行插入操作
            ContentValues values = new ContentValues();
            values.put("phone_number", phoneNumber);
            values.put("ID", report.getID());
            values.put("detail", report.getDetail());
            if (report.getPicture() == null) {
                values.put("report_picture", "");
            } else {
                values.put("report_picture", report.getPicture().toString());
            }
            values.put("report_type", report.getReport_type());
            values.put("hospital", report.getHospital());
            values.put("report_date", report.getReport_date());
            long rowsAffected = db.insert("report", null, values);
            return rowsAffected > 0;
        }
    }

    public Boolean insertOrUpdateRecord(String phoneNumber, Record record) {
        // 检查数据库中是否存在指定的记录
        Cursor cursor = db.query("record", null, "ID = ? AND phone_number=?", new String[]{String.valueOf(record.getID()), phoneNumber}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            // 数据库中存在记录，执行更新操作
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
            int rowsAffected = db.update("record", values, "ID = ? AND phone_number=?", new String[]{String.valueOf(record.getID()), phoneNumber});
            cursor.close();
            return rowsAffected > 0;
        } else {
            // 数据库中不存在记录，执行插入操作
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
    }

    public Boolean insertOrUpdateAlert(String phoneNumber, Alert alert) {
        // 检查数据库中是否存在指定的记录
        Cursor cursor = db.query("alert", null, "ID=? AND phone_number=?", new String[]{String.valueOf(alert.getID()), phoneNumber}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            // 数据库中存在记录，执行更新操作
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
            cursor.close();
            return rowsAffected > 0;
        } else {
            // 数据库中不存在记录，执行插入操作
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

    public boolean insertRecords(String phoneNumber, List<Record> records) {
        boolean success = true;

        for (Record record : records) {
            ContentValues values = new ContentValues();
            values.put("phone_number", phoneNumber);
            values.put("record_date", record.getRecord_date());
            values.put("hospital", record.getHospital());
            values.put("doctor", record.getDoctor());
            values.put("organ", record.getOrgan());
            values.put("conclusion", record.getConclusion());
            values.put("symptom", record.getSymptom());
            values.put("suggestion", record.getSuggestion());

            long rowsAffected = db.insert("record", null, values);
            success &= rowsAffected > 0;
        }

        return success;
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
        int rowsAffected = db.update("record", values, "ID = ? AND phone_number=?", new String[]{String.valueOf(record.getID()), phoneNumber});
        return rowsAffected > 0;
    }

    public Boolean deleteRecord(String phoneNumber, int ID) {
        int rowsAffected = db.delete("record", "ID = ? AND phone_number = ?", new String[]{String.valueOf(ID), phoneNumber});
        return rowsAffected > 0;
    }

    public Boolean deleteRecords(String phoneNumber) {
        int rowsAffected = db.delete("record", "phone_number = ?", new String[]{phoneNumber});
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
                report.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
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
        values.put("detail", report.getDetail());
        if (report.getPicture() == null) {
            values.put("picture", "");
        } else {
            values.put("picture", report.getPicture().toString());
        }
        values.put("report_type", report.getReport_type());
        values.put("hospital", report.getHospital());
        values.put("report_date", report.getReport_date());
        long rowsAffected = db.insert("report", null, values);
        return rowsAffected > 0;
    }

    public boolean insertReports(String phoneNumber, List<Report> reports) {
        boolean success = true;

        for (Report report : reports) {
            ContentValues values = new ContentValues();
            values.put("phone_number", phoneNumber);
            values.put("detail", report.getDetail());

            // 请注意这里的判断和处理图片的方式，可以根据实际情况修改
            if (report.getPicture() == null) {
                values.put("picture", "");
            } else {
                values.put("picture", report.getPicture().toString());
            }

            values.put("report_type", report.getReport_type());
            values.put("hospital", report.getHospital());
            values.put("report_date", report.getReport_date());

            long rowsAffected = db.insert("report", null, values);
            success &= rowsAffected > 0;
        }

        return success;
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

    public Boolean deleteReports(String phoneNumber) {
        int flag = db.delete("report", "phone_number=?", new String[]{phoneNumber});
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
        //依靠bug运行
        //这个ID不好删，它用在提醒页的显示上了，它靠的是ID唯一性，点击提醒就会添加失败，删了会点一次添加一次
        values.put("ID",alert.getID());
        values.put("alert_date", alert.getAlert_date());
        values.put("alert_cycle", alert.getAlert_cycle());
        values.put("advice", alert.getAdvice());
        values.put("alert_type", alert.getAlert_type());
        values.put("title", alert.getTitle());
        values.put("is_medicine", alert.getIs_medicine());
        long rowsAffected = db.insert("alert", null, values);
        return rowsAffected > 0;
    }

    public boolean insertAlerts(String phoneNumber, List<Alert> alerts) {
        boolean success = true;

        for (Alert alert : alerts) {
            ContentValues values = new ContentValues();
            values.put("phone_number", phoneNumber);
            values.put("alert_date", alert.getAlert_date());
            values.put("alert_cycle", alert.getAlert_cycle());
            values.put("advice", alert.getAdvice());
            values.put("alert_type", alert.getAlert_type());
            values.put("title", alert.getTitle());
            values.put("is_medicine", alert.getIs_medicine());

            long rowsAffected = db.insert("alert", null, values);
            success &= rowsAffected > 0;
        }

        return success;
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

    public Boolean deleteAlert(String phoneNumber, int ID) {
        int flag = db.delete("alert","ID=? AND phone_number=?", new String[]{String.valueOf(ID), phoneNumber});
        return flag > 0;
    }

    public Boolean deleteAlerts(String phoneNumber) {
        int flag = db.delete("alert", "phone_number=?", new String[]{phoneNumber});
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

    public static Alert getAlert(ArrayList<Alert> alertArrayList, int ID) {
        Stream<Alert> alertStream = alertArrayList.stream();
        Alert alert = alertStream.filter(e -> e.getID() == ID).collect(Collectors.toList()).get(0);
        return alert;
    }

    public static Record getHistory(ArrayList<Record> recordList, int ID) {
        Stream<Record> historyStream = recordList.stream();
        Record record = historyStream.filter(e -> e.getID() == ID).collect(Collectors.toList()).get(0);
        return record;
    }

    public static Report gerReport(ArrayList<Report> reportList, int ID) {
        Stream<Report> reportStream = reportList.stream();
        Report report = reportStream.filter(e -> e.getID() == ID).collect(Collectors.toList()).get(0);
        return report;
    }
}
