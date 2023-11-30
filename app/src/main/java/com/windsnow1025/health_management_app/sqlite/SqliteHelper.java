package com.windsnow1025.health_management_app.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "Android.db";
    private static final int VERSION = 6;

    public SqliteHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    private static final String CREATE_USER = """
                CREATE TABLE user (
                    phone_number TEXT PRIMARY KEY,
                    name TEXT,
                    email TEXT,
                    birthday TEXT,
                    sex TEXT
                )
            """;

    private static final String CREATE_RECORD = """
                CREATE TABLE history (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    phone_number TEXT,
                    record_date TEXT,
                    hospital TEXT,
                    doctor TEXT,
                    organ TEXT,
                    symptom TEXT,
                    conclusion TEXT,
                    suggestion TEXT,
                    FOREIGN KEY (phone_number) REFERENCES user(phone_number)
                )
            """;

    private static final String CREATE_REPORT = """
                CREATE TABLE report (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    phone_number TEXT,
                    report_date TEXT,
                    hospital TEXT,
                    report_type TEXT,
                    picture BLOB,
                    detail TEXT,
                    FOREIGN KEY (phone_number) REFERENCES user(phone_number)
                )
            """;

    private static final String CREATE_ALERT = """
                CREATE TABLE alert (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    phone_number TEXT,
                    alert_type TEXT,
                    advice TEXT,
                    title TEXT,
                    alert_date TEXT,
                    alert_cycle TEXT,
                    is_medicine TEXT,
                    FOREIGN KEY (phone_number) REFERENCES user(phone_number)
                )
            """;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_REPORT);
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_ALERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS report");
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS alert");
        onCreate(db);
    }
}
