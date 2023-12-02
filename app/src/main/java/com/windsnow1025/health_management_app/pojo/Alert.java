package com.windsnow1025.health_management_app.pojo;

public class Alert {
    private int id;
    private int record_id;
    private int report_id;
    private String phone_number;
    private String alert_type;
    private String advice;
    private String title;
    private String alert_date;
    private String alert_cycle;
    private String is_medicine;

    public Alert() {
    }

    public Alert(int id, int record_id, int report_id, String phone_number, String alert_type, String advice, String title, String alert_date, String alert_cycle, String is_medicine) {
        this.id = id;
        this.record_id = record_id;
        this.report_id = report_id;
        this.phone_number = phone_number;
        this.alert_type = alert_type;
        this.advice = advice;
        this.title = title;
        this.alert_date = alert_date;
        this.alert_cycle = alert_cycle;
        this.is_medicine = is_medicine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getReport_id() {
        return report_id;
    }

    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAlert_type() {
        return alert_type;
    }

    public void setAlert_type(String alert_type) {
        this.alert_type = alert_type;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlert_date() {
        return alert_date;
    }

    public void setAlert_date(String alert_date) {
        this.alert_date = alert_date;
    }

    public String getAlert_cycle() {
        return alert_cycle;
    }

    public void setAlert_cycle(String alert_cycle) {
        this.alert_cycle = alert_cycle;
    }

    public String getIs_medicine() {
        return is_medicine;
    }

    public void setIs_medicine(String is_medicine) {
        this.is_medicine = is_medicine;
    }
}
