package com.example.user.demotide20;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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


public class SystemActivity extends AppCompatActivity {
    String cUserName,today;
    //商品同步API
    String url = "http://demo.shinda.com.tw/ModernWebApi/getProduct.aspx";
    //建立一個類別存JSON
    //資料庫名稱
    final String DB_NAME = "tblTable";
    //商品資訊資料庫建立
    MyDBhelper helper;MyDBhelper2 helper2;
    SQLiteDatabase db,db2,db3;
    ContentValues addbase;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        toolBar();
        getPreviousPage();
        setUpDateTime();
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
                    Log.e("json",json);
                    parseJson(json);
                }
                //解析JSON 放入SQL
                private void parseJson(String json) {
                    ArrayList<ProductInfo> trans = new ArrayList<ProductInfo>();
                    try {
                        JSONArray array = new JSONArray(json);
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
    //立即同步商品鍵
    public void upthing (View v){
        //建立SQL
        setThingSQL();
        //先刪除舊有資料表格
        db.delete(DB_NAME, null, null);
        //放入新增表格
        Get get = new Get();
        get.start();
        //用來紀錄更新日期和次數
        upDateTimes();
    }
    //刪除全部商品鍵
    public void delThing(View v){
        setThingSQL();
        db.delete(DB_NAME,null,null);
    }
    //返回鍵 暫時用來看資料庫內容
    public void back (View v){
        setThingSQL();
        cursor3();
    }
    //登出鍵
    public void out (View v){
        Intent intent = new Intent(SystemActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //查詢資料庫 並把查詢結果放進listView
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
    //把要更新的時間放入SQL裡
    private void setTime(String timeUp){
        MyDBhelper3 myDB3 = new MyDBhelper3(SystemActivity.this,"tblTable3",null,1);
        db3=myDB3.getWritableDatabase();
        ContentValues addbase = new ContentValues();
        addbase.put("timeUp",timeUp);
        db3.insert("tblTable3",null,addbase);
    }
    //設定更新時間
    private void setUpDateTime(){
        ArrayAdapter<String> upTime = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        //upTime.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        upTime.add("請選擇");
        upTime.add("08:00");
        upTime.add("12:00");
        upTime.add("18:00");
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(upTime);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //所點擊的索引值
                int index = spinner.getSelectedItemPosition();
                Log.e("SPINNER", String.valueOf(index));
                if (index==1){
                    String timeUp="18:30";
                    setTime(timeUp);
                }
                else if(index==2){
                    String timeUp="12:00";
                    setTime(timeUp);
                }
                else if(index==3){
                    String timeUp="18:00";
                    setTime(timeUp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
}
