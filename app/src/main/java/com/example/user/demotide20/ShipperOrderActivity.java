package com.example.user.demotide20;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.user.demotide20.R.layout.lview3;

public class ShipperOrderActivity extends AppCompatActivity {
    String cUserName, cUserID, order, checked, cProductIDeSQL,newbase64;
    String url = "http://demo.shinda.com.tw/ModernWebApi/Pickup.aspx";
    LinearLayout linear;
    ArrayAdapter list;
    ListView listView;
    int addNum = 0;
    String[] stringArray;
    Bitmap Abitmap,Bbitmap,Cbitmap,Dbitmap,Ebitmap;
    String Abase64,Bbase64,Cbase64,Dbase64,Ebase64;
    ArrayList trans, trans2, Btrans;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    byte[] AArray,BArray,CArray,DArray,EArray;
    final String[] activity = {"換人檢", "結案"};
    ArrayList allbase64;
    //顯示用
    public class ProductInfo {
        private String mProductName;
        private String mProductID;
        private int mQty = 0;
        private int mNowQty = 0;

        //建構子
        ProductInfo(final String ProductName, final String ProductID, final int Qty, int NowQty) {
            this.mProductName = ProductName;
            this.mProductID = ProductID;
            this.mQty = Qty;
            this.mNowQty = NowQty;

        }

        //方法
        @Override
        public String toString() {
            return this.mProductName + "(" + this.mProductID + ")" + this.mQty + "(" + this.mNowQty + ")";
        }
    }

    //POST用
    public class ProductInfo2 {
        private String mProductID;
        private int mNowQty = 0;

        //建構子
        ProductInfo2(final String ProductID, int NowQty) {
            this.mProductID = ProductID;
            this.mNowQty = NowQty;

        }

        //方法
        @Override
        public String toString() {
            return "{\"ProductNo\":\"" + this.mProductID + "\",\"NowQty\":" + this.mNowQty + "}";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order);
        getPreviousPage();
        toolBar();
        setSwitch();
        Post post = new Post();
        post.start();
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
                Intent intent = new Intent(ShipperOrderActivity.this, ShipperActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID", cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                ShipperOrderActivity.this.finish();
            }
        });
    }

    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        /**
         * TakePicture的資料
         */
        AArray = bag.getByteArray("AArray");
        BArray = bag.getByteArray("BArray");
        CArray = bag.getByteArray("CArray");
        DArray = bag.getByteArray("DArray");
        EArray = bag.getByteArray("EArray");

        /**
         * ShipperActivity的資料
         */
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        order = bag.getString("order", null);
        checked = bag.getString("checked", null);

        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
        TextView textView1 = (TextView) findViewById(R.id.textView11);
        textView1.setText(order);
    }

    //設定 Switch功能
    private void setSwitch() {
        //switch 設定
        Switch sw = (Switch) findViewById(R.id.switch2);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //switch 點擊
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //點擊後 lineat就會顯示並且addNum=1
                if (isChecked) {
                    linear = (LinearLayout) findViewById(R.id.linear);
                    //顯示
                    linear.setVisibility(View.VISIBLE);
                    addNum = 1;
                }
                //沒有點擊 addNum=0
                else {
                    //關閉
                    linear.setVisibility(View.INVISIBLE);
                    addNum = 0;
                }

            }
        });
    }

    //打開Switch的按鍵
    public void add1(View v) {
        addNum = 1;
    }

    public void add5(View v) {
        addNum = 5;
    }

    public void add10(View v) {
        addNum = 10;
    }

    public void addAll(View v) {
        addNum = 99999;
    }

    // 執行緒 - 執行PostUserInfo()方法
    class Post extends Thread {
        String cProductName;

        @Override
        public void run() {
            PostOrderThingsInfo();
        }

        private void PostOrderThingsInfo() {
            //post
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"dopickups\",\"UserID\":\"" + cUserID + "\",\"PickupNumbers\":\"" + checked + "\"}";
            Log.e("POST的JSON", json);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //POST後 回傳的JSON檔
                    String json = response.body().string();
                    Log.e("回傳的JSON", json);
                    String json2 = null;
                    try {
                        JSONObject j = new JSONObject(json);
                        json2 = j.getString("PickUpProducts");
                        Log.e("取出PickUpProducts", json2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    parseJson(json2);

                }

                //解析取出PickUpProducts的值
                private void parseJson(String json2) {
                    Log.e("json22", json2);
                    trans = new ArrayList();
                    trans2 = new ArrayList();

                    ArrayList<Map<String, String>> myList = new ArrayList<Map<String, String>>();
                    try {
                        final JSONArray array = new JSONArray(json2);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            //開啟資料庫 用ProductNo比對SQL的cProductID
                            setThingSQL();
                            Cursor c = db.query("tblTable",                            // 資料表名字
                                    null,                                              // 要取出的欄位資料
                                    "cProductID=?",                                    // 查詢條件式(WHERE)
                                    new String[]{obj.optString("ProductNo")},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                                    null,                                              // Group By字串語法
                                    null,                                              // Having字串法
                                    null);                                             // Order By字串語法(排序)

                            while (c.moveToNext()) {
                                cProductName = c.getString(c.getColumnIndex("cProductName"));
                                Log.e("cProductName", cProductName);
                            }
                            //用自訂類別 把JSONArray的值取出來
                            Map<String, String> map = new HashMap<String, String>();



                            map.put("NowQty",obj.optString("NowQty"));
                            map.put("ProductNo",obj.optString("ProductNo"));
                            map.put("cProductName",cProductName);
                            map.put("Qty", obj.optString("Qty"));
                            myList.add(map);
                            Log.e("mylist", String.valueOf(myList));
                            trans.add(new ProductInfo(cProductName, obj.optString("ProductNo"), obj.optInt("Qty"), obj.optInt("NowQty")));
                            trans2.add(new ProductInfo2(obj.optString("ProductNo"), obj.optInt("NowQty")));
                            Log.e("trans", String.valueOf(trans));
                            Log.e("trans2", String.valueOf(trans2));
                            db.close();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listView = (ListView) findViewById(R.id.list);
                    final IconAdapter gAdapter = new IconAdapter();
                    //list.setAdapter(gAdapter);
                    //list = new ArrayAdapter(ShipperOrderActivity.this, android.R.layout.simple_list_item_1, trans);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(gAdapter);

                        }
                    });
                }
            });
        }

    }
    //arraylist 自訂 listView
    class IconAdapter extends BaseAdapter {

        //ProductInfo[] func = (ProductInfo[]) trans.toArray(new ProductInfo[trans.size()]);

        //int陣列方式將功能儲存在icons陣列
        //int[] icons = {};
        @Override
        public int getCount() {
            return trans.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //設定listView
            View v = convertView;
            if(v == null){
                v =getLayoutInflater().inflate(lview3,null);
                //ImageView image = (ImageView)v.findViewById(R.id.img);
                TextView text = (TextView)v.findViewById(R.id.textView5);

                //呼叫setImageResource方法設定圖示的圖檔資源
                //image.setImageResource(icons[position]);
                //呼叫setText方法設定圖示上的文字
                text.setText(String.valueOf(trans.get(position)));
            }
            return v;

        }

    }

    //商品清單SQL
    private void setThingSQL() {
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        db = helper.getWritableDatabase();
    }

    //商品條碼SQL
    private void setBarcodeSQL() {
        helper4 = new MyDBhelper4(this, "tblTable4", null, 1);
        db4 = helper4.getWritableDatabase();
    }

    private void cBarcode() {
        Btrans = new ArrayList();
        EditText editText = (EditText) findViewById(R.id.editText);
        String barcode = editText.getText().toString();
        Log.e("barcode", barcode);
        setBarcodeSQL();
        Cursor c = db4.query("tblTable4",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                "cBarcode=?",                                    // 查詢條件式(WHERE)
                new String[]{barcode},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductIDeSQL = c.getString(c.getColumnIndex("cProductID"));
            Log.e("cBarcode", cProductIDeSQL);
            Btrans.add(cProductIDeSQL);

        }

        int i = c.getCount();
        Log.e("筆數", String.valueOf(i));
        if (i == 0) {
            Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
        } else if (i == 1) {
            Toast.makeText(this, cProductIDeSQL, Toast.LENGTH_SHORT).show();
        } else if (i > 1) {
            stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
            //stringArray = (String[]) Btrans.toArray(new String[0]);
            chooseThings();
            db4.close();
        }

    }

    public void enter(View v) {
        cBarcode();
    }

    private void chooseThings() {
        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
        builder.setTitle("請選擇商品編號");
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "You clicked "+stringArray[i], Toast.LENGTH_SHORT).show();
                Log.e("點擊",stringArray[i]);
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void onPicture (View v){
        String activity = "Shipper";
        Intent intent = new Intent(ShipperOrderActivity.this,TakePictures.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName",cUserName);
        bag.putString("cUserID",cUserID);
        bag.putString("activity",activity);
        bag.putString("checked", checked);
        bag.putString("order",order);
        intent.putExtras(bag);
        startActivity(intent);
        ShipperOrderActivity.this.finish();
    }
    private void AmakeBase64(){
        Abitmap = BitmapFactory.decodeByteArray(AArray, 0, AArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Abitmap.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        Abase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Abase64",Abase64);
    }
    private void BmakeBase64(){
        Bbitmap = BitmapFactory.decodeByteArray(BArray, 0, BArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        Bbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Bbase64",Bbase64);
    }
    private void CmakeBase64(){
        Cbitmap = BitmapFactory.decodeByteArray(CArray, 0, CArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Cbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        Cbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Cbase64",Cbase64);
    }
    private void DmakeBase64(){
        Dbitmap = BitmapFactory.decodeByteArray(DArray, 0, DArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Dbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        Dbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Dbase64",Dbase64);
    }
    private void EmakeBase64(){
        Ebitmap = BitmapFactory.decodeByteArray(EArray, 0, EArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Ebitmap.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        Ebase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Ebase64",Ebase64);
    }
    private void AllBase64(){
        if(AArray!=null){
            AmakeBase64();
        }
        if(BArray!=null){
            BmakeBase64();
        }
        if(CArray!=null){
            CmakeBase64();
        }
        if(DArray!=null){
            DmakeBase64();
        }
        if(EArray!=null){
            EmakeBase64();
        }


        allbase64 = new ArrayList();
        allbase64.add(Abase64);
        allbase64.add(Bbase64);
        allbase64.add(Cbase64);
        allbase64.add(Dbitmap);
        allbase64.add(Ebase64);

    }
    public void onActivity(View v){
        chooseActivity();
    }
    private void chooseActivity(){
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        dialog_list.setTitle("挑選照片");
        dialog_list.setItems(activity, new DialogInterface.OnClickListener() {
            @Override
            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(ShipperOrderActivity.this, "你選的是" + activity[which], Toast.LENGTH_SHORT).show();
                Log.e("選取", activity[which]);
                Log.e("選取數字", String.valueOf(which));
                if (which == 0) {

                }
                else if(which ==1) {

                    AllBase64();
                    Log.e("allbase64", String.valueOf(allbase64));
                }

            }
        });
        dialog_list.show();
    }




}