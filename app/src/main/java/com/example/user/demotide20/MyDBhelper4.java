package com.example.user.demotide20;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/4/11.
 * 條碼比對商品資訊
 */

public class MyDBhelper4 extends SQLiteOpenHelper {
    public MyDBhelper4(SystemActivity context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBhelper4(Delay delay, String db_name, Object factory, int version) {
        super(delay, db_name, (SQLiteDatabase.CursorFactory) factory, version);
    }

    public MyDBhelper4(ShipperOrderActivity shipperOrderActivity, String tblTable4, Object o, int i) {
        super(shipperOrderActivity, tblTable4, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper4(PurchaseOrderActivity purchaseOrderActivity, String tblTable4, Object o, int i) {
        super(purchaseOrderActivity, tblTable4, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper4(StorageOrderActivity storageOrderActivity, String tblTable4, Object o, int i) {
        super(storageOrderActivity, tblTable4, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper4(BlackSingleActivity blackSingleActivity, String tblTable4, Object o, int i) {
        super(blackSingleActivity, tblTable4, (SQLiteDatabase.CursorFactory) o, i);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable4 (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "cProductID TEXT, "
                        + "cBarcode TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}