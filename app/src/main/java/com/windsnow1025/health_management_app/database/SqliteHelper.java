package com.windsnow1025.health_management_app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "health-management-app.db";
    private static final int VERSION = 14;

    public SqliteHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    private static final String CREATE_USER = """
                CREATE TABLE user (
                    phone_number TEXT PRIMARY KEY,
                    username TEXT,
                    birthday TEXT,
                    sex TEXT
                )
            """;

    private static final String CREATE_RECORD = """
                    CREATE TABLE record (
                        id INTEGER PRIMARY KEY,
                        phone_number TEXT,
                        record_date TEXT,
                        hospital TEXT,
                        doctor TEXT,
                        organ TEXT,
                        symptom TEXT,
                        conclusion TEXT,
                        suggestion TEXT,
                        UNIQUE (id, phone_number),
                        FOREIGN KEY (phone_number) REFERENCES user(phone_number)
                    )
            """;

    private static final String CREATE_REPORT = """
                    CREATE TABLE report (
                        id INTEGER PRIMARY KEY,
                        phone_number TEXT,
                        report_date TEXT,
                        hospital TEXT,
                        report_type TEXT,
                        picture BLOB,
                        detail TEXT,
                        UNIQUE (id, phone_number),
                        FOREIGN KEY (phone_number) REFERENCES user(phone_number)
                    )
            """;

    private static final String CREATE_ALERT = """
                CREATE TABLE alert (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    record_id INTEGER,
                    report_id INTEGER,
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

    private static final String CREATE_GOODS = """
                CREATE TABLE goods (
                    name TEXT,
                    price REAL,
                    imageResourceId INTEGER
                )
            """;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_REPORT);
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_ALERT);
        db.execSQL(CREATE_GOODS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS report");
        db.execSQL("DROP TABLE IF EXISTS record");
        db.execSQL("DROP TABLE IF EXISTS alert");
        db.execSQL("DROP TABLE IF EXISTS goods");
        onCreate(db);
    }
}
