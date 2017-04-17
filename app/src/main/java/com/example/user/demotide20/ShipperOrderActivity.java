package com.example.user.demotide20;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
    int addNum = 0,iMax=0;
    String[] stringArray;
    Bitmap Abitmap,Bbitmap,Cbitmap,Dbitmap,Ebitmap;
    String Abase64,Bbase64,Cbase64,Dbase64,Ebase64;
    ArrayList<Map<String, String>> myList,upList;
    ArrayList trans, trans2, Btrans;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    byte[] AArray,BArray,CArray,DArray,EArray;
    final String[] activity = {"換人檢", "結案"};
    ArrayList AllImgUri;
    Map<String, String> map;
    SimpleAdapter simpleAdapter;
    SpecialAdapter adapter;
    Uri imgUri,AImgUri,BImgUri,CImgUri,DImgUri,EImgUri;
    Map<String, String> newMap;
    ArrayList upUri;
    final String[] newStringArray = new String[1];
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
        addNum = 99999;
    }
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
                                 //Log.e("json22", json2);
                                 trans = new ArrayList();
                                 trans2 = new ArrayList();

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
                                         //Log.e("mylist", String.valueOf(myList));
                                         //Log.e("map", String.valueOf(map));
                                         trans.add(new ProductInfo(cProductName, obj.optString("ProductNo"), obj.optInt("Qty"), obj.optInt("NowQty")));
                                         trans2.add(new ProductInfo2(obj.optString("ProductNo"), obj.optInt("NowQty")));
                                         //Log.e("trans", String.valueOf(trans));
                                         //Log.e("trans2", String.valueOf(trans2));

                                         db.close();
                                     }
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }

                                 listView = (ListView) findViewById(R.id.list);
                                 //SimpleAdapter 顯示

                                 /*
                                 simpleAdapter = new SimpleAdapter(ShipperOrderActivity.this,
                                         myList,
                                         R.layout.lview4,
                                         new String[]{"cProductName", "ProductNo", "Qty", "NowQty"},
                                         new int[]{R.id.textView21, R.id.textView22, R.id.textView23, R.id.textView24});
                                 */
                                         adapter = new SpecialAdapter(
                                         ShipperOrderActivity.this,
                                         myList,
                                         R.layout.lview4,
                                         new String[]{"cProductName", "ProductNo", "Qty", "NowQty"},
                                         new int[]{R.id.textView21, R.id.textView22, R.id.textView23, R.id.textView24});
                    //final IconAdapter gAdapter = new IconAdapter();
                    //list.setAdapter(gAdapter);
                    //list = new ArrayAdapter(ShipperOrderActivity.this, android.R.layout.simple_list_item_1, trans);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //listView.setAdapter(gAdapter);
                            listView.setAdapter(adapter);

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

            if (cProductIDeSQL.equals(map.get("ProductNo"))) {
                Toast.makeText(this, map.get("ProductNo"), Toast.LENGTH_SHORT).show();
                int i2 = Integer.parseInt(map.get("NowQty"));
                i2++;
                Log.e("I2", String.valueOf(i2));
                map.remove("NowQty");
                map.put("NowQty", String.valueOf(i2));
                simpleAdapter.notifyDataSetChanged();
            }



        } else if (i > 1) {
            stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
            //stringArray = (String[]) Btrans.toArray(new String[0]);
            chooseThings();


        }
    }
    public void enter(View v) {
        //cBarcode();

        EditText editText1 = (EditText)findViewById(R.id.editText);
        String getText = editText1.getText().toString();
        Log.e("GETTEXT",getText);
        for(int i=0; i < iMax; i++){
            Log.e("NEWTEXT",myList.get(i).get("ProductNo"));
            if(getText.equals(myList.get(i).get("ProductNo"))){
                Toast.makeText(this,myList.get(i).get("ProductNo"), Toast.LENGTH_SHORT).show();
                int i2 = Integer.parseInt(myList.get(i).get("NowQty"));
                i2++;
                Log.e("I2", String.valueOf(i2));
                map.put("NowQty", String.valueOf(i2));
                Log.e("map", String.valueOf(map));

                newMap = new HashMap<String, String>();
                newMap.put("NowQty", String.valueOf(i2));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName",myList.get(i).get("cProductName") );
                newMap.put("Qty",myList.get(i).get("Qty") );
                myList.set(i,newMap);
                //myList.remove(i).get("NowQty");
                //Log.e("myList",myList.remove(i).get("NowQty"));
                adapter.notifyDataSetChanged();
            }
        }



    }

    private void chooseThings() {

        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
        builder.setTitle("請選擇商品編號");
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "You clicked "+stringArray[i], Toast.LENGTH_SHORT).show();
                Log.e("點擊",stringArray[i]);
                newStringArray[0] = stringArray[i];
                Log.e("點擊2",newStringArray[0]);
                Log.e("PRODUCTNO",map.get("ProductNo"));
                if (newStringArray[0].equals(map.get("ProductNo"))) {
                    int i2 = Integer.parseInt(map.get("NowQty"));
                    Toast.makeText(ShipperOrderActivity.this, map.get("ProductNo")+"1", Toast.LENGTH_SHORT).show();
                    i2++;
                    Log.e("I2", String.valueOf(i2));
                    map.remove("NowQty");
                    map.put("NowQty", String.valueOf(i2));
                    simpleAdapter.notifyDataSetChanged();
                }
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
        bag.putStringArrayList("AllImgUri",AllImgUri);
        intent.putExtras(bag);
        startActivity(intent);
        ShipperOrderActivity.this.finish();
    }

    private void AllBase64() {
        Log.e("AllImgUri", String.valueOf(AllImgUri));
        checkUri();
        Map<String, String> upMap;

        upList = new ArrayList<Map<String, String>>();
        Log.e("myList", String.valueOf(myList));
        Log.e("size", String.valueOf(myList.size()));
        for(int i=0; i < myList.size(); i++){
            upMap = new HashMap<String, String>();
            upMap.put("NowQty", myList.get(i).get("NowQty"));
            upMap.put("ProductNo", myList.get(i).get("ProductNo"));
            upList.add(upMap);
            }
        Log.e("upList", String.valueOf(upList));

    }

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
        Log.e("Abase64",Abase64);
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
    }
    public void onActivity(View v){
        chooseActivity();
    }
    private void chooseActivity(){
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        dialog_list.setTitle("動作");
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
                    PostInfo post = new PostInfo();
                    post.start();
                }

            }
        });
        dialog_list.show();
    }

    class PostInfo extends Thread{
        @Override
        public void run() {
            PostUserInfo();
        }
    }
    //把輸入的帳號密碼轉成JSON 用OkHttp Post登入API
    private void PostUserInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"PickupNumbers\" :\"S20160000004,S20160000014\",\"PickupProducts\":"+upList+",\"imgbase64\": "+"["+Abase64+"]"+"}";
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
                Log.e("POST後的回傳值", json);

            }
        });
    }


}