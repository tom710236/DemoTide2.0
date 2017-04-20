package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShipperOrderActivity extends AppCompatActivity {
    String cUserName, cUserID, order, checked, cProductIDeSQL;
    String url = "http://demo.shinda.com.tw/ModernWebApi/Pickup.aspx";
    LinearLayout linear;
    ListView listView;
    int addNum = 0,iMax=0;
    String[] stringArray;
    String Abase64,Bbase64,Cbase64,Dbase64,Ebase64;
    ArrayList<Map<String, String>> myList,upList;
    ArrayList trans, trans2, Btrans;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    final String[] activity = {"換人檢", "結案"};
    ArrayList AllImgUri,Allbase64;
    Map<String, String> map;
    SpecialAdapter adapter;
    Uri AImgUri,BImgUri,CImgUri,DImgUri,EImgUri;
    Map<String, String> newMap;
    int getint;
    String upStringList;
    final String[] newStringArray = new String[1];
    ProgressDialog pd;
    public class ProductIDInfo{
        private String mProductID;

        ProductIDInfo(String ProductID){
            this.mProductID = ProductID;
        }
        public String toString(){
            return mProductID;
        }
    }
    public class ProductNameInfo{
        private String mProductName;

        ProductNameInfo(String ProductID){
            this.mProductName = ProductID;
        }
        public String toString(){
            return mProductName;
        }
    }
    public class QtyInfo{
        private String mmQty;

        QtyInfo(String ProductID){
            this.mmQty = ProductID;
        }
        public String toString(){
            return mmQty;
        }
    }
    public class NowQtyInfo{
        private String mNowQty;

        NowQtyInfo(String ProductID){
            this.mNowQty = ProductID;
        }
        public String toString(){
            return mNowQty;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order);
        //取得上一頁資料
        getPreviousPage();
        //toolBar設定
        toolBar();
        //Switch設定
        setSwitch();
        //Post後回傳放入listView
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
                back();
            }
        });
    }
    private void back(){
        Intent intent = new Intent(ShipperOrderActivity.this, ShipperActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID", cUserID);
        intent.putExtras(bag);
        startActivity(intent);
        ShipperOrderActivity.this.finish();
    }

    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();

        AllImgUri = bag.getStringArrayList("AllImgUri");
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
        addNum = 999999;
    }
    //改變listView(SimpleAdapter) item的顏色
    public class SpecialAdapter extends SimpleAdapter {
        private int[] colors = new int[] { 0x30ffffff, 0x30696969 };

        public SpecialAdapter(Context context, ArrayList<Map<String, String>> items, int resource, String[] from, int[] to) {
            super(context, items, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            int colorPos = position % colors.length;
            view.setBackgroundColor(colors[colorPos]);
            return view;
        }
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
                                 //Log.e("回傳的JSON", json);
                                 String json2 = null;
                                 try {
                                     JSONObject j = new JSONObject(json);
                                     json2 = j.getString("PickUpProducts");
                                     //Log.e("取出PickUpProducts", json2);
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                                 parseJson(json2);

                             }

                             //解析取出PickUpProducts的值
                             private void parseJson(String json2) {

                                 myList = new ArrayList<Map<String, String>>();
                                 try {
                                     final JSONArray array = new JSONArray(json2);
                                     for (iMax = 0; iMax < array.length(); iMax++) {
                                         JSONObject obj = array.getJSONObject(iMax);
                                         //開啟資料庫 用ProductNo比對SQL的cProductID
                                         setThingSQL();
                                         Cursor c = db.query("tblTable",                            // 資料表名字
                                                 null,                                              // 要取出的欄位資料
                                                 "cProductID=?",                                    // 查詢條件式(WHERE)
                                                 new String[]{obj.optString("ProductNo")},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                                                 null,                                              // Group By字串語法
                                                 null,                                              // Having字串法
                                                 null);                                             // Order By字串語法(排序)

                                         while (c.moveToNext()) {
                                             cProductName = c.getString(c.getColumnIndex("cProductName"));
                                             //Log.e("cProductName", cProductName);
                                         }
                                         //用自訂類別 把JSONArray的值取出來

                                         map = new HashMap<String, String>();
                                         map.put("NowQty", String.valueOf(new NowQtyInfo(obj.optString("NowQty"))));
                                         map.put("ProductNo", String.valueOf(new ProductIDInfo(obj.getString("ProductNo"))));
                                         map.put("cProductName", String.valueOf(new ProductNameInfo(cProductName)));
                                         map.put("Qty", String.valueOf(new QtyInfo(obj.getString("Qty"))));
                                         myList.add(map);
                                         db.close();
                                     }
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }

                                 listView = (ListView) findViewById(R.id.list);
                                         adapter = new SpecialAdapter(
                                         ShipperOrderActivity.this,
                                         myList,
                                         R.layout.lview4,
                                         new String[]{"cProductName", "ProductNo", "Qty", "NowQty"},
                                         new int[]{R.id.textView21, R.id.textView22, R.id.textView23, R.id.textView24});

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);

                        }
                    });
                }
            });
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
    //判斷條碼
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
        //條碼找不到商品編號
        if (i == 0) {
            Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
        //條碼找到一筆商品編號
        } else if (i == 1) {
            //先判斷條碼內的商品號碼是否有在listView裡
            if(checkID()==true){
                //Switch 關閉時
                if (addNum == 0) {
                    //跳出輸入數字對話框
                    final View item = LayoutInflater.from(ShipperOrderActivity.this).inflate(R.layout.item, null);
                    new AlertDialog.Builder(ShipperOrderActivity.this)
                            .setTitle("請輸入數量")
                            .setView(item)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) item.findViewById(R.id.editText2);
                                    //如果有輸入數字 執行setNOWQty
                                    if(editText.length()!=0){
                                        getint = Integer.parseInt(editText.getText().toString());
                                        //判斷有無商品代碼 並帶入數字
                                        setNOWQty(getint);
                                    }


                                }
                            }).show();
                }else if(addNum ==1){
                    setNOWQty(addNum);
                }else if(addNum ==5) {
                    setNOWQty(addNum);
                }else if(addNum ==10) {
                    setNOWQty(addNum);
                }else if(addNum == 999999){
                    setNOWQty(addNum);
                }
            }else {
                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
            }
          //條碼找到一筆以上商品編號
        } else if (i > 1) {
            stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
            chooseThings();
        }
    }
    //判斷條碼內的商品是否有在list裡 有就回傳true
    private boolean checkID() {
        for (int i3 = 0; i3 < iMax; i3++) {
            if(cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))){
                return true;
            }

        }
        return false;
    }
    private boolean checkID2(){
        for (int i3 = 0; i3 < iMax; i3++) {
            if(newStringArray[0].equals(myList.get(i3).get("ProductNo"))){
                return true;
            }

        }
        return false;
    }
    private void setNOWQty(int getint2){
        for (int i3 = 0; i3 < iMax; i3++) {
            if (cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("NowQty"));
                int i4 = Integer.parseInt(myList.get(i3).get("Qty"));
                Log.e("I22", String.valueOf(i2));
                Log.e("I44", String.valueOf(i4));
                //數量
                if(getint2!=1){
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                    } else {
                        if(i2==1){
                            i2=getint2;
                        }else {
                            i2 = i2 + getint2;
                        }
                    }
                }else{
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                    }else {
                            i2 = i2 + getint2;
                        }
                }


                Log.e("I2", String.valueOf(i2));
                newMap = new HashMap<String, String>();
                newMap.put("NowQty", String.valueOf(i2));
                newMap.put("ProductNo", myList.get(i3).get("ProductNo"));
                newMap.put("cProductName", myList.get(i3).get("cProductName"));
                newMap.put("Qty", myList.get(i3).get("Qty"));
                myList.set(i3, newMap);
                //myList.remove(i).get("NowQty");
                //Log.e("myList",myList.remove(i).get("NowQty"));
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void setNOWQty2(int getint2){
        for (int i3 = 0; i3 < iMax; i3++) {
            if (newStringArray[0].equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("NowQty"));
                int i4 = Integer.parseInt(myList.get(i3).get("Qty"));
                Log.e("I22", String.valueOf(i2));
                Log.e("I44", String.valueOf(i4));
                //數量
                if(getint2!=1){
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                    } else {
                        if(i2==1){
                            i2=getint2;
                        }else {
                            i2 = i2 + getint2;
                        }
                    }
                }else{
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                    }else {
                        i2 = i2 + getint2;
                    }
                }


                Log.e("I2", String.valueOf(i2));
                newMap = new HashMap<String, String>();
                newMap.put("NowQty", String.valueOf(i2));
                newMap.put("ProductNo", myList.get(i3).get("ProductNo"));
                newMap.put("cProductName", myList.get(i3).get("cProductName"));
                newMap.put("Qty", myList.get(i3).get("Qty"));
                myList.set(i3, newMap);
                //myList.remove(i).get("NowQty");
                //Log.e("myList",myList.remove(i).get("NowQty"));
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void addNOWQty(){
        if(checkID2()==true){
            if (addNum == 0) {
                final View item = LayoutInflater.from(ShipperOrderActivity.this).inflate(R.layout.item, null);
                new AlertDialog.Builder(ShipperOrderActivity.this)
                        .setTitle("請輸入數量")
                        .setView(item)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.editText2);
                                if (editText.length() != 0) {
                                    getint = Integer.parseInt(editText.getText().toString());
                                    setNOWQty2(getint);
                                }
                            }
                        }).show();

            }else if(addNum==1){
                setNOWQty2(addNum);
            }else if(addNum==5){
                setNOWQty2(addNum);
            }else if(addNum==10) {
                setNOWQty2(addNum);
            }else if(addNum==999999) {
                setNOWQty2(addNum);
            }
        }else{
            Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
        }

    }

    //按確定後 所執行
    public void enter(View v) {
        cBarcode();
    }
    //輸入的條碼 有兩個以上商品 跳出對話框 選擇商品
    private void chooseThings() {

        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
        builder.setTitle("請選擇商品編號");
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("點擊",stringArray[i]);
                newStringArray[0] = stringArray[i];
                Log.e("點擊2",newStringArray[0]);
                Log.e("PRODUCTNO",map.get("ProductNo"));
                addNOWQty();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    //拍照按鍵 切換到拍照頁面 並把所需的資料傳遞過去
    public void onPicture (View v){
        String activity = "Shipper";
        Intent intent = new Intent(ShipperOrderActivity.this,TakePictures.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName",cUserName);
        bag.putString("cUserID",cUserID);
        //判斷是從哪一頁傳過去的
        bag.putString("activity",activity);
        bag.putString("checked", checked);
        bag.putString("order",order);
        //把拍照頁的Uri回傳回去(讓照片不消失)
        bag.putStringArrayList("AllImgUri",AllImgUri);
        intent.putExtras(bag);
        startActivity(intent);
        ShipperOrderActivity.this.finish();
    }
    //從myList取出ProductNo NowQty 放入upList POST用
    private void AllBase64() {
        Log.e("AllImgUri", String.valueOf(AllImgUri));

        Map<String, String> upMap;

        upList = new ArrayList<Map<String, String>>();
        for(int i=0; i < myList.size(); i++){
            //LinkedHashMap<String, String>() 會依照put的順序
            upMap = new LinkedHashMap<String, String>();
            upMap.put("\"ProductNo\"","\"" +myList.get(i).get("ProductNo")+ "\"" );
            upMap.put("\"NowQty\"", myList.get(i).get("NowQty"));
            upList.add(upMap);
            }
        Log.e("upList", String.valueOf(upList));
        String upString = String.valueOf(upList).replaceAll("=", ":");
        upStringList = upString.replaceAll(", ",",");
        Log.e("upStringList", String.valueOf(upStringList));

    }
    // 假如拍照頁面傳過來的AllImgUri不為空值 執行Uri轉換
    private void checkUri(){

        if (AllImgUri != null && !AllImgUri.isEmpty()) {
            if (AllImgUri.get(0) != null) {
                AImgUri = (Uri) AllImgUri.get(0);
                AImgUriBase64(AImgUri);

            }
            if (AllImgUri.get(1) != null) {
                BImgUri = (Uri) AllImgUri.get(1);
                BImgUriBase64(BImgUri);
            }
            if (AllImgUri.get(2) != null) {
                CImgUri = (Uri) AllImgUri.get(2);
                CImgUriBase64(CImgUri);
            }
            if (AllImgUri.get(3) != null) {
                DImgUri = (Uri) AllImgUri.get(3);
                DImgUriBase64(DImgUri);
            }
            if (AllImgUri.get(4) != null) {
                EImgUri = (Uri) AllImgUri.get(4);
                EImgUriBase64(EImgUri);
            }

        }

    }
    // Uri 轉成Bitmap 再轉成 base64
    // bitmap 要轉成 jpg 然後上傳時要給提示 (未做)
    void AImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔

        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Abase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Allbase64 = new ArrayList();
        Allbase64.add("\"" +Abase64+"\"");
    }
    void BImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔


        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Bbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Bbase64",Bbase64);
        Allbase64 = new ArrayList();
        Allbase64.add("\"" +Bbase64+"\"");
    }
    void CImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔



        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Cbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Cbase64",Cbase64);
        Allbase64 = new ArrayList();
        Allbase64.add("\"" +Cbase64+"\"");
    }
    void DImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔


        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Dbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Dbase64",Dbase64);
        Allbase64 = new ArrayList();
        Allbase64.add("\"" +Dbase64+"\"");
    }
    void EImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔


        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Ebase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Ebase64",Ebase64);
        Allbase64 = new ArrayList();
        Allbase64.add("\"" +Ebase64+"\"");
    }
    //動作按鍵
    public void onActivity(View v){
        chooseActivity();
    }
    //動作按鍵的方法 (選擇檢貨或換人檢)
    private void chooseActivity(){
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        dialog_list.setTitle("動作");
        dialog_list.setItems(activity, new DialogInterface.OnClickListener() {
            @Override
            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Log.e("選取", activity[which]);
                Log.e("選取數字", String.valueOf(which));
                //換人檢
                if (which == 0) {
                    AllBase64();
                    PostChangeInfo post = new PostChangeInfo();
                    post.start();

                }
                //結案
                else if(which ==1) {
                    checkUri();
                    AllBase64();
                    PostEndInfo post = new PostEndInfo();
                    post.start();
                    /* 顯示ProgressDialog */
                    pd = ProgressDialog.show(ShipperOrderActivity.this, "換人檢", "上傳中，請稍後...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            spandTimeMethod();
                            handler.sendEmptyMessage(0);
                        }

                    }).start();
                }
            }
        });
        dialog_list.show();
    }
    private void spandTimeMethod() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /* 顯示ProgressDialog */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();
        }
    };
    //結案
    class PostEndInfo extends Thread{
        @Override
        public void run() {
            PostendInfo();
        }
    }
    //結案 用OkHttp PostAPI
    private void PostendInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"PickupNumbers\" :\""+ checked +"\",\"PickupProducts\":"+upStringList+",\"imgbase64\": "+Allbase64+"}";
        Log.e("POST",json);
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            //post 失敗後執行
            @Override
            public void onFailure(Call call, IOException e) {
                //非主執行緒顯示UI(Toast)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShipperOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                Log.e("結案後POST的回傳值", json);
                //Toast.makeText(ShipperOrderActivity.this, json, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //換人檢
    class PostChangeInfo extends Thread{
        @Override
        public void run() {
            PostChangeInfo();
        }
    }
    //換人檢 用OkHttp PostAPI
    private void PostChangeInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"PickupNumbers\" :\""+ checked +"\",\"PickupProducts\":"+upStringList+"}";
        Log.e("POST",json);
        RequestBody body = RequestBody.create(JSON,json);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            //post 失敗後執行
            @Override
            public void onFailure(Call call, IOException e) {
                //非主執行緒顯示UI(Toast)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShipperOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                Log.e("POST後的回傳值", json);
                changeEnd(json);
            }

            private void changeEnd(String json) {
                int result = 0;
                try {
                    result = new JSONObject(json).getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result==1){
                    back();
                }
            }
        });
    }


}