package com.example.user.demotide20;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/4/10.
 * 商品資訊
 */

public class MyDBhelper extends SQLiteOpenHelper {
    public MyDBhelper(SystemActivity context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBhelper(Delay delay, String db_name, Object factory, int version) {
        super(delay, db_name, (SQLiteDatabase.CursorFactory) factory, version);
    }

    public MyDBhelper(ShipperOrderActivity shipperOrderActivity, String db_name, Object o, int i) {
        super(shipperOrderActivity, db_name, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper(PurchaseOrderActivity purchaseOrderActivity, String db_name, Object o2, int i) {
        super(purchaseOrderActivity, db_name, (SQLiteDatabase.CursorFactory) o2, i);
    }

    public MyDBhelper(StorageOrderActivity storageOrderActivity, String db_name, Object o, int i) {
        super(storageOrderActivity, db_name, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper(BlackSingleActivity blackSingleActivity, String db_name, Object o, int i) {
        super(blackSingleActivity, db_name, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper(AllListActivity allListActivity, String tblTable, Object o, int i) {
        super(allListActivity, tblTable, (SQLiteDatabase.CursorFactory) o, i);
    }

    public MyDBhelper(SearchBlackSingleListActivity searchBlackSingleListActivity, String db_name, Object o, int i) {
        super(searchBlackSingleListActivity, db_name, (SQLiteDatabase.CursorFactory) o, i);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "cProductID TEXT, "
                        + "cProductName TEXT, "
                        + "cGoodsNo TEXT, "
                        + "cUpdateDT TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
