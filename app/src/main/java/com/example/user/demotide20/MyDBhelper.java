package com.example.user.demotide20;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TOM on 2017/4/10.
 * 商品資訊
 */

public class MyDBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;	//資料庫版本
    public MyDBhelper(SystemActivity context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, DATABASE_VERSION);
    }

    public MyDBhelper(Delay delay, String db_name, Object factory, int version) {
        super(delay, db_name, (SQLiteDatabase.CursorFactory) factory, DATABASE_VERSION);
    }

    public MyDBhelper(ShipperOrderActivity shipperOrderActivity, String db_name, Object o, int i) {
        super(shipperOrderActivity, db_name, (SQLiteDatabase.CursorFactory) o, DATABASE_VERSION);
    }

    public MyDBhelper(PurchaseOrderActivity purchaseOrderActivity, String db_name, Object o2, int i) {
        super(purchaseOrderActivity, db_name, (SQLiteDatabase.CursorFactory) o2, DATABASE_VERSION);
    }

    public MyDBhelper(StorageOrderActivity storageOrderActivity, String db_name, Object o, int i) {
        super(storageOrderActivity, db_name, (SQLiteDatabase.CursorFactory) o, DATABASE_VERSION);
    }

    public MyDBhelper(BlackSingleActivity blackSingleActivity, String db_name, Object o, int i) {
        super(blackSingleActivity, db_name, (SQLiteDatabase.CursorFactory) o, DATABASE_VERSION);
    }

    public MyDBhelper(AllListActivity allListActivity, String tblTable, Object o, int i) {
        super(allListActivity, tblTable, (SQLiteDatabase.CursorFactory) o, DATABASE_VERSION);
    }

    public MyDBhelper(SearchBlackSingleListActivity searchBlackSingleListActivity, String db_name, Object o, int i) {
        super(searchBlackSingleListActivity, db_name, (SQLiteDatabase.CursorFactory) o, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create =
                ("CREATE TABLE tblTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "cProductID TEXT, "
                        + "cProductName TEXT, "
                        + "cGoodsNo TEXT, "
                        + "cProductShortName TEXT, "
                        + "cSort TEXT, "
                        + "cUpdateDT TEXT);");
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tblTable");	//刪除舊有的資料表
        //db.execSQL("ALTER TABLE tblTask ADD COLUMN cCash VARCHAR");
        onCreate(db);
    }

}
