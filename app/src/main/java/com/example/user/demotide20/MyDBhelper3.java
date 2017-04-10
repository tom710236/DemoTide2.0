package com.example.user.demotide20;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/4/10.
 * 排定更新的時間
 */

public class MyDBhelper3 extends SQLiteOpenHelper {
    public MyDBhelper3 (SystemActivity context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBhelper3(Delay context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable3 (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "timeUp TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
