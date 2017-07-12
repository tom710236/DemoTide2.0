package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.R.attr.name;
import static android.icu.text.Normalizer.NO;
import static android.os.Build.ID;


public class SystemActivity extends AppCompatActivity {
    String cUserName,today,tblTable4,cUserID;
    //商品同步API
    String url = "http://demo.shinda.com.tw/ModernWebApi/getProduct.aspx";
    //建立一個類別存JSON
    //資料庫名稱
    final String DB_NAME = "tblTable";
    Spinner spinner;
    ArrayAdapter<String> upTime;
    String timeUp2=null;
    //商品資訊資料庫建立
    MyDBhelper helper;MyDBhelper2 helper2;MyDBhelper4 helper4;MyDBhelper3 helper3;
    SQLiteDatabase db,db2,db3,db4;
    ContentValues addbase,addbase2;
    ProgressDialog d;
    int upDateNumI ;
    int upDateNumL ;

    //String ID,name,NO,DT;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        //設定toolBar
        toolBar();
        //取得上一頁傳來的資訊
        getPreviousPage();
        //Spinner 資料同步時間
        setUpDateTime();
    }

    //GET 商品清單資料 並放入資料庫
    class Get extends Thread{
        @Override
        public void run() {
            okHttpGet();
        }

        // Get 更新資訊的方法
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
                        final JSONArray array = new JSONArray(json2);


                        //setThingSQL();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            trans.add(new ProductInfo(obj.optString("cProductID"), obj.optString("cProductName"),obj.optString("cGoodsNo"),obj.optString("cUpdateDT")));
                            String ID = obj.optString("cProductID");
                            String name = obj.optString("cProductName");
                            String NO = obj.optString("cGoodsNo");
                            String DT = obj.optString("cUpdateDT");
                            Log.e("ID", trans.get(i).cProductID);
                            //建立SQL
                            /*
                            setThingSQL();

                            //放入SQL
                            addbase = new ContentValues();
                            addbase.put("cProductID", ID);
                            addbase.put("cProductName", name);
                            addbase.put("cGoodsNo", NO);
                            addbase.put("cUpdateDT", DT);
                            db.insert(DB_NAME, null, addbase);

                            db.close();
                                */

                        }
                        // 把資料放入資料庫(之前一個一個放太慢,整個抓下來後再一口氣放入)
                        setThingSQL();
                        db.beginTransaction();
                        try {
                            addbase = new ContentValues();
                            for(int i =0;i<trans.size();i++ ){

                                addbase.put("cProductID", trans.get(i).cProductID);
                                addbase.put("cProductName", trans.get(i).cProductName);
                                addbase.put("cGoodsNo", trans.get(i).cGoodsNo);
                                addbase.put("cUpdateDT", trans.get(i).cUpdateDT);
                                db.insert(DB_NAME, null, addbase);
                            }
                            db.setTransactionSuccessful();
                        }finally {
                            db.endTransaction();
                        }

                        String json3 = new JSONObject(json).getString("ProductBarcodes");
                        Log.e("JSON3",json3);
                        final JSONArray array1 = new JSONArray(json3);

                        for (int i = 0; i < array1.length(); i++) {
                            JSONObject obj = array1.getJSONObject(i);
                            trans2.add(new BarcodesInfo(obj.optString("cProductID"), obj.optString("cBarcode")));
                            String BID = obj.optString("cProductID");
                            Log.e("BID",BID);
                            String Bcode = obj.optString("cBarcode");
                            Log.e("Bcode",Bcode);
                            //建立SQL
                            /*
                            setBarcodeSQL();

                            //放入SQL
                            addbase2 = new ContentValues();
                            addbase2.put("cProductID", BID);
                            addbase2.put("cBarcode", Bcode);
                            db4.insert("tblTable4", null, addbase2);

                            db4.close();
                                */

                        }
                        setBarcodeSQL();
                        db4.beginTransaction();

                        try {
                            addbase2 = new ContentValues();
                            for(int i = 0; i<trans2.size();i++){

                                addbase2.put("cProductID", trans2.get(i).cProductID);
                                addbase2.put("cBarcode", trans2.get(i).cBarcode);
                                db4.insert("tblTable4", null, addbase2);
                            }
                            db4.setTransactionSuccessful();
                        }finally {
                            db4.endTransaction();
                        }



                        handler.sendEmptyMessage(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SystemActivity.this, "已同步完畢", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
    //立即同步商品鍵
    public void upthing (View v){
        //建立商品資訊SQL
        setThingSQL();
        //先刪除舊有資料表格
        db.delete(DB_NAME, null, null);
        //建立條碼比對商品資訊SQL
        setBarcodeSQL();
        //先刪除舊有資料表格
        db4.delete("tblTable4",null,null);
        //放入新增表格(商品清單)
        //setWait();
        d = ProgressDialog.show(SystemActivity.this, "更新中...", "", false);
        Get get = new Get();
        get.start();
        //用來紀錄更新日期和次數
        upDateTimes();
        db4.close();

    }
    //刪除全部商品鍵
    public void delThing(View v){
        setThingSQL();
        db.delete(DB_NAME,null,null);
        db.close();
        setBarcodeSQL();
        db4.delete("tblTable4",null,null);
        db4.close();
        Toast.makeText(this, "商品已刪除", Toast.LENGTH_SHORT).show();
    }
    //返回鍵 暫時用來看資料庫內容
    public void back (View v){
        //setThingSQL();
        //cursor3();
        //setBarcodeSQL();

        cursor4();
    }
    //登出鍵
    public void out (View v){
        Intent intent = new Intent(SystemActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //查詢商品清單資料庫 並把查詢結果放進listView
    private void cursor3(){

        Cursor c=db.rawQuery("SELECT * FROM "+DB_NAME, null);
        ListView lv = (ListView)findViewById(R.id.listView);
        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(this,
                //android.R.layout.simple_expandable_list_item_2,
                R.layout.lview2,
                c,
                //new String[] {"info","amount"},
                new String[] {"_id", "cProductID", "cProductName", "cGoodsNo", "cUpdateDT"},
                //new int[] {android.R.id.text1,android.R.id.text2},
                new int[] {R.id.textView19,R.id.textView18,R.id.textView17,R.id.textView16,R.id.textView15},
                0);
        lv.setAdapter(adapter);
    }
    //查詢商品條碼資料庫 並把查詢結果放進listView
    private void cursor4(){
        MyDBhelper3 MyDB3 = new MyDBhelper3(SystemActivity.this,"tblTable3",null,1);
        db3=MyDB3.getWritableDatabase();
        Cursor c=db3.rawQuery("SELECT * FROM "+"tblTable3", null);
        ListView lv = (ListView)findViewById(R.id.listView);
        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(this,
                //android.R.layout.simple_expandable_list_item_2,
                R.layout.lview2,
                c,
                //new String[] {"info","amount"},
                new String[] {"_id", "timeUp"},
                //new int[] {android.R.id.text1,android.R.id.text2},
                new int[] {R.id.textView19,R.id.textView18},
                0);
        lv.setAdapter(adapter);
    }
    //把要更新的時間放入SQL裡
    private void setTime(String timeUp){
        MyDBhelper3 myDB3 = new MyDBhelper3(SystemActivity.this,"tblTable3",null,1);
        db3=myDB3.getWritableDatabase();
        db3.delete("tblTable3", null, null);
        ContentValues addbase = new ContentValues();
        addbase.put("timeUp",timeUp);
        Log.e("addbase", String.valueOf(addbase));
        db3.insert("tblTable3",null,addbase);
        db3.close();


    }
    //設定更新時間
    private void setUpDateTime(){
        upTime = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        //upTime.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        upTime.add("請選擇");
        upTime.add("08:00");
        upTime.add("12:00");
        upTime.add("18:00");
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(upTime);
        if(getTimeUp()!=null){
                if(getTimeUp().equals("08:00")){
                    spinner.setSelection(1);
                    upTime.notifyDataSetChanged();
                }else if(getTimeUp().equals("12:00")) {
                    spinner.setSelection(2);
                    upTime.notifyDataSetChanged();
                }else if (getTimeUp().equals("18:00")) {
                 spinner.setSelection(3);
                    upTime.notifyDataSetChanged();
                }
        }else{
            spinner.setSelection(0);
            upTime.notifyDataSetChanged();
             }

        //設定Spinner的預設index
        //spinner.setSelection(indexSpinner);
        //upTime.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //所點擊的索引值
                int indexSpinner = spinner.getSelectedItemPosition();
                if (indexSpinner==1){
                    String timeUp="08:00";
                    setTime(timeUp);
                }
                else if(indexSpinner==2){
                    String timeUp="12:00";
                    setTime(timeUp);
                }
                else if(indexSpinner==3){
                    String timeUp="18:00";
                    setTime(timeUp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //商品條碼SQL
    private void setBarcodeSQL(){
        helper4 = new MyDBhelper4(this, "tblTable4" , null, 1);
        //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
        db4 = helper4.getWritableDatabase();
    }
    //商品清單SQL
    private void setThingSQL(){
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
        db = helper.getWritableDatabase();
    }
    //更新日期和次數SQL
    private void setDateSQL(){
        helper2 = new MyDBhelper2(this,"tblOrder2",null,1);
        db2=helper2.getWritableDatabase();
    }
    //設定toolBar
    private void toolBar() {
        //Toolbar 設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //回到上一頁的圖示
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        //回到上一頁按鍵設定
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //因為cUserName從上一頁傳過來了 所以要回到上一頁 要把cUserName再傳回去
                Intent intent = new Intent(SystemActivity.this, AllListActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID",cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                SystemActivity.this.finish();
            }
        });
    }
    //取得上一頁回傳的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID",null);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
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
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            return true;
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }
    //到排定更新的時間的SQL 去得到更新的時間
    private String getTimeUp(){
        helper3 = new MyDBhelper3(SystemActivity.this,"tblTable3",null,1);
        db3 = helper3.getWritableDatabase();
        Cursor c=db3.rawQuery("SELECT * FROM "+"tblTable3", null);   //查詢全部欄位
        /*
        Cursor c = db3.query("tblTable3",                          // 資料表名字
                null,                                              // 要取出的欄位資料
                null,                                              // 查詢條件式(WHERE)
                null,                                              // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)
        //往下一個 收尋
        */
        while(c.moveToNext()) {
            timeUp2 = c.getString(c.getColumnIndex("timeUp"));
        }
        //Log.e("timeUp2",timeUp2);
        return timeUp2;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            d.dismiss();
        }
    };
    private void setWait(){
        d=new ProgressDialog(SystemActivity.this);
        d.setMessage("同步中..");
        d.show();
    }

}
