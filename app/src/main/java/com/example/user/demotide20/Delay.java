package com.example.user.demotide20;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TOM on 2017/4/10.
 * Service 背景處理
 */

public class Delay extends Service {
    //宣告
    Runnable runnable;
    Handler handler;
    //商品同步API
    String url = "http://demo.shinda.com.tw/ModernWebApi/getProduct.aspx";
    private MyDBhelper helper;
    private MyDBhelper2 helper2;
    private MyDBhelper4 helper4;
    SQLiteDatabase db,db2,db3,db4;
    final String DB_NAME = "tblTable";
    String tblTable4;
    ContentValues addbase,addbase2;
    String today,timeUp2="07:00";
    public class ProductInfo {
        private String cProductID;
        private String cProductName;
        private String cGoodsNo;
        private String cUpdateDT;

        //建構子
        ProductInfo(final String ProductID, final String ProductName, final String GoodsNo,final String UpdateDT) {
            this.cProductID = ProductID;
            this.cProductName = ProductName;
            this.cGoodsNo = GoodsNo;
            this.cUpdateDT = UpdateDT;

        }
        //方法
        @Override
        public String toString() {
            return this.cProductID +  this.cProductName  + this.cGoodsNo + this.cUpdateDT;
        }
    }
    public class BarcodesInfo {
        private String cProductID;
        private String cBarcode;


        //建構子
        BarcodesInfo(final String ProductID, final String Barcode) {
            this.cProductID = ProductID;
            this.cBarcode = Barcode;


        }
        //方法
        @Override
        public String toString() {
            return this.cProductID + this.cBarcode;
        }
    }
    @Override
    //啟動後
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getTimeUp();
                Log.e("timeUp222",timeUp2);
                timeUp();
                if(today.equals(timeUp2)){
                    //建立商品清單SQL
                    setThingSQL();
                    //刪除舊有清單
                    db.delete(DB_NAME, null, null);
                    //放入新增表格
                    Get get = new Get();
                    get.start();
                    upDateTimes();
                }
                //每分鐘執行一次
                handler.postAtTime(this,android.os.SystemClock.uptimeMillis()+60*1000);

            }
        };
        //每分鐘執行一次
        handler.postAtTime(runnable,android.os.SystemClock.uptimeMillis()+60*1000);
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }
    //stopService後
    //執行Log.e("STOP","STOP") ,handler.removeCallbacks(runnable);
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("STOP","STOP");
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //得到現在的時間(分和秒)
    private void timeUp(){
        Calendar mCal = Calendar.getInstance();
        String dateformat = "HH:mm";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today = df.format(mCal.getTime());
    }
    //到排定更新的時間的SQL 去得到需要更新的時間
    private String getTimeUp(){
        MyDBhelper3 MyDB3 = new MyDBhelper3(Delay.this,"tblTable3",null,1);
        db3=MyDB3.getWritableDatabase();
        //Cursor c=db2.rawQuery("SELECT * FROM "+"tblTable2", null);   //查詢全部欄位
        Cursor c = db3.query("tblTable3",                          // 資料表名字
                null,                                              // 要取出的欄位資料
                null,                                              // 查詢條件式(WHERE)
                null,                                              // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)
        //往下一個 收尋
        while(c.moveToNext()) {
            timeUp2 = c.getString(c.getColumnIndex("timeUp"));
        }
        Log.e("timeUp22",timeUp2);
        return timeUp2;
    }
    //商品清單SQL
    private void setThingSQL(){
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
        db = helper.getWritableDatabase();
    }
    //GET 商品清單資料 並放入資料庫
    class Get extends Thread{
        @Override
        public void run() {
            okHttpGet();
        }

        // Get
        private void okHttpGet(){
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback(){

                @Override
                public void onFailure(Call call, IOException e) {

                }
                //把get到的資料(JSON)轉為字串 並執行parseJson方法
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("商品清單回傳",json);
                    parseJson(json);
                }
                //解析JSON 放入SQL
                private void parseJson(String json) {

                    ArrayList<ProductInfo> trans = new ArrayList<ProductInfo>();
                    ArrayList<BarcodesInfo> trans2 = new ArrayList<>();
                    try {
                        String json2 = new JSONObject(json).getString("Products");
                        Log.e("JSON2",json2);
                        JSONArray array = new JSONArray(json2);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            trans.add(new ProductInfo(obj.optString("cProductID"), obj.optString("cProductName"),obj.optString("cGoodsNo"),obj.optString("cUpdateDT")));
                            String ID = obj.optString("cProductID");
                            String name = obj.optString("cProductName");
                            String NO = obj.optString("cGoodsNo");
                            String DT = obj.optString("cUpdateDT");
                            //建立SQL
                            setThingSQL();
                            //放入SQL
                            addbase = new ContentValues();
                            addbase.put("cProductID", ID);
                            addbase.put("cProductName", name);
                            addbase.put("cGoodsNo", NO);
                            addbase.put("cUpdateDT", DT);
                            db.insert(DB_NAME, null, addbase);
                            db.close();

                        }
                        String json3 = new JSONObject(json).getString("ProductBarcodes");
                        Log.e("JSON3",json3);
                        JSONArray array1 = new JSONArray(json3);
                        for (int i = 0; i < array1.length(); i++) {
                            JSONObject obj = array1.getJSONObject(i);
                            trans2.add(new BarcodesInfo(obj.optString("cProductID"), obj.optString("cBarcode")));
                            String BID = obj.optString("cProductID");
                            Log.e("BID",BID);
                            String Bcode = obj.optString("cBarcode");
                            Log.e("Bcode",Bcode);
                            //建立SQL
                            setBarcodeSQL();
                            //放入SQL
                            addbase2 = new ContentValues();
                            addbase2.put("cProductID", BID);
                            addbase2.put("cBarcode", Bcode);
                            db4.insert("tblTable4", null, addbase2);
                            db4.close();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
    //紀錄更新日期和次數
    private void upDateTimes() {
        //建立一個SQL
        setDateSQL();
        ContentValues addbase = new ContentValues();
        //得到現在時間
        time();
        //把時間放入"cUpdateDT"
        addbase.put("cUpdateDT",today);
        //插入資料
        db2.insert("tblTable2",null,addbase);
        db2.close();
    }
    //得到現在時間
    private void time() {
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy/MM/dd/ HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today = df.format(mCal.getTime());
    }
    //更新日期和次數SQL
    private void setDateSQL(){
        helper2 = new MyDBhelper2(this,"tblOrder2",null,1);
        db2=helper2.getWritableDatabase();
    }
    //商品條碼SQL
    private void setBarcodeSQL(){
        helper4 = new MyDBhelper4(this, "tblTable4" , null, 1);
        //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
        db4 = helper4.getWritableDatabase();
    }
}
