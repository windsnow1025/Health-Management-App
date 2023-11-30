package com.windsnow1025.health_management_app.pojo;

import java.io.Serializable;

public class User implements Serializable {
    String phone_number;
    String username;
    String birthday;
    String sex;

    public User() {
    }

    public User(String phone_number, String username, String birthday, String sex) {
        this.phone_number = phone_number;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
