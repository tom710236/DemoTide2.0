package com.example.user.demotide20;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.user.demotide20.R.id.checkBox4;
import static com.example.user.demotide20.R.id.checkBox5;
import static com.example.user.demotide20.R.id.editText7;
import static com.example.user.demotide20.R.id.textView21;
import static com.example.user.demotide20.R.id.textView23;
import static com.example.user.demotide20.R.id.textView24;
import static com.example.user.demotide20.R.layout.lview4;

public class ShipperOrderActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {
    String cUserName, cUserID, order, checked, cProductIDeSQL;
    String url = "http://demo.shinda.com.tw/ModernWebApi/Pickup.aspx";
    //String url = "192.168.0.2:8011/Pickup.aspx";
    LinearLayout linear;
    ListView listView,listView2;
    int check = 0;
    int addNum = 0, iMax = 0;
    int indexSpinner;
    int checkInt = 0 ;
    String[] stringArray;
    String Abase64, Bbase64, Cbase64, Dbase64, Ebase64;
    ArrayList<LinkedHashMap<String, String>> myList, upList, myList2;
    ArrayList  Btrans;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    final String[] activity = {"換人檢", "結案"};
    ArrayList  Allbase64;
    LinkedHashMap<String, String> map;
    SpecialAdapter adapter,adapter2;
    Uri AImgUri, BImgUri, CImgUri, DImgUri, EImgUri;
    LinkedHashMap<String, String> newMap;
    int getint;
    String upStringList;
    final String[] newStringArray = new String[1];
    ProgressDialog d;
    int iCheck,iCheck2;
    int iMatch=0;
    private HashSet<Integer> mCheckSet = new HashSet<Integer>();
    //
    Uri imgUri;    //用來參照拍照存檔的 Uri 物件
    Bitmap bmp;
    int PicInt = 0,PicADD = 0;
    int addInt = 0;
    ProgressDialog myDialog;

    String today,today2;
    String logToday=null,logBarcode=null,logProductName = null,logProductID=null,logQty=null,logNowQty=null,logAdd=null; //設定文件檔名裡面的內容
    int mSoundID ;
    SoundPool mSoundPool;

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    //String fileName = "my_file2.txt";// LOG的文件檔名
    //String data = "時間:"+logToday+",條碼:"+logBarcode+",商品名稱:"+logProductID+",訂單數量:"+logQty+",檢貨數量:"+logNowQty+",增加數量:"+logAdd+"\r\n"; //文件檔名裡面的內容
    public class ProductIDInfo {
        private String mProductID;

        ProductIDInfo(String ProductID) {
            this.mProductID = ProductID;
        }

        public String toString() {
            return mProductID;
        }
    }

    public class ProductNameInfo {
        private String mProductName;

        ProductNameInfo(String ProductID) {
            this.mProductName = ProductID;
        }

        public String toString() {
            return mProductName;
        }
    }

    public class QtyInfo {
        private String mmQty;

        QtyInfo(String ProductID) {
            this.mmQty = ProductID;
        }

        public String toString() {
            return mmQty;
        }
    }

    public class NowQtyInfo {
        private String mNowQty;

        NowQtyInfo(String ProductID) {
            this.mNowQty = ProductID;
        }

        public String toString() {
            return mNowQty;
        }
    }
    //toolbar 旁邊的三個點
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.xml.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*
            case R.id.action_search:
                //TODO search
                LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
                LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
                LinTop.setVisibility(View.VISIBLE);
                linear.setVisibility(View.INVISIBLE);

                break;
            case R.id.action_search2:
                //TODO search
                LinearLayout LinTop2 = (LinearLayout)findViewById(R.id.LinTop);
                LinearLayout linear2 = (LinearLayout)findViewById(R.id.linear);
                LinTop2.setVisibility(View.GONE);
                linear2.setVisibility(View.GONE);
                break;
                */
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order);
        //final EditText editText = (EditText) findViewById(R.id.editText);
        //Android 對 EditText 取得 focus
        //editText.requestFocus();

        //判斷外部空間狀態
        if(isExternalStorageWritable()){
            //可寫
            Log.e("外部空間狀態","可寫");

        } else if(isExternalStorageReadable()){
            //可讀
            Log.e("外部空間狀態","可讀");
        } else{
            //不可寫不可讀
            Log.e("外部空間狀態","不可寫不可讀");
        }
        //音效設定
        mSoundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
        mSoundPool.setOnLoadCompleteListener(ShipperOrderActivity.this);
        mSoundID = mSoundPool.load (this, R.raw.windows_8_notify,1);

        //取得上一頁資料
        getPreviousPage();
        //toolBar設定
        toolBar();
        //Switch設定
        setSwitch();
        //Post後回傳放入listView
        Post post = new Post();
        post.start();
        setDialog();
        setEditText();
        setEditText2();
        setCheckBox ();



    }

    //設定toolBar
    private void toolBar() {
        //Toolbar 設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //回到上一頁的圖示
        //toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        //回到上一頁按鍵設定
        /*
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //因為cUserName從上一頁傳過來了 所以要回到上一頁 要把cUserName再傳回去
                back();
            }
        });
            */


    }

    private void back() {
        Intent intent = new Intent(ShipperOrderActivity.this, ShipperActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID", cUserID);
        bag.putInt("indexSpinner",indexSpinner);
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

        //AllImgUri = bag.getStringArrayList("AllImgUri");
        /**
         * ShipperActivity的資料
         */
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        order = bag.getString("order", null);
        checked = bag.getString("checked", null);
        indexSpinner = bag.getInt("indexSpinner",0);
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
                    iMatch = 0;
                    PicADD = 1;
                }
                //沒有點擊 addNum=0
                else {
                    //關閉
                    linear.setVisibility(View.GONE);
                    addNum = 0;
                    PicADD = 0;
                }

            }
        });

    }

    //打開Switch的按鍵
    public void add1(View v) {
        addNum = 1;
        if (iCheck==1){
            setNOWQty(addNum);
        }else if(iCheck>1){
            setNOWQty2(addNum);
        }
        iMatch=0;
    }
    public void add5(View v) {
        addNum = 5;
        if (iMatch==1){
            addNum=4;
            if (iCheck==1){
                setNOWQty(addNum);
            }else if(iCheck>1){
                setNOWQty2(addNum);
            }
        }else{
            if (iCheck==1){
                setNOWQty(addNum);
            }else if(iCheck>1) {
                setNOWQty2(addNum);
            }
        }
        iMatch=0;
    }
    public void add10(View v) {
        addNum = 10;
        if (iMatch == 1) {
            addNum = 9;
            if (iCheck == 1) {
                setNOWQty(addNum);
            } else if (iCheck > 1) {
                setNOWQty2(addNum);
            }
        } else {
            if (iCheck == 1) {
                setNOWQty(addNum);
            } else if (iCheck > 1) {
                setNOWQty2(addNum);
            }
        }
        iMatch = 0;
    }
    public void addAll(View v) {
        addNum = 999999;
        if (iMatch==1){
            addNum=999998;
            if (iCheck==1){
                setNOWQty(addNum);
            }else if(iCheck>1){
                setNOWQty2(addNum);
            }
        }else {
            if (iCheck==1){
                setNOWQty(addNum);
            }else if(iCheck>1) {
                setNOWQty2(addNum);
            }
        }
        iMatch=0;
    }

    //改變listView(SimpleAdapter) item的顏色
    public class SpecialAdapter extends SimpleAdapter {
        //背景顏色
        private int[] colors = new int[]{0x30ffffff, 0x30696969};
        //檢滿後字體顏色
        private int colors2 = Color.BLUE;

        public SpecialAdapter(Context context, ArrayList<LinkedHashMap<String, String>> items, int resource, String[] from, int[] to) {
            super(context, items, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            int colorPos = position % colors.length;
            view.setBackgroundColor(colors[colorPos]);
            convertView = null;
            //要先初始化顏色 不然往下拉時 item會被吃掉
            if (convertView == null) {
                TextView textView21 = (TextView) view.findViewById(R.id.textView21);
                textView21.setTextColor(Color.BLACK);
                TextView textView22 = (TextView) view.findViewById(R.id.textView22);
                textView22.setTextColor(Color.BLACK);
                TextView textView23 = (TextView) view.findViewById(R.id.textView23);
                textView23.setTextColor(Color.BLACK);
                TextView textView24 = (TextView) view.findViewById(R.id.textView24);
                textView24.setTextColor(Color.BLACK);
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox4);
                //checkBox.setChecked(mCheckSet.contains(position));

                //數量=總量時便item變顏色
                for (int i = 0; i < myList.size(); i++) {
                    if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {

                        if (position == i) {
                            //view.setBackgroundColor(colors2);
                            //return view;
                            textView21.setTextColor(colors2);
                            textView22.setTextColor(colors2);
                            textView23.setTextColor(colors2);
                            textView24.setTextColor(colors2);

                        }

                    }

                }
                //checkBox若勾選 則檢貨數量=訂單數量
                checkBox.setChecked(mCheckSet.contains(position));
                checkBox.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 沒有勾勾的時候點擊
                        if (((CheckBox) v).isChecked()) {
                            if(myList.get(position).get("NowQty").equals(myList.get(position).get("Qty"))){
                                newMap = new LinkedHashMap<String, String>();
                                newMap.put("NowQty", "0");
                                newMap.put("ProductNo", myList.get(position).get("ProductNo"));
                                newMap.put("cProductName", myList.get(position).get("cProductName"));
                                newMap.put("Qty", myList.get(position).get("Qty"));
                                myList.set(position, newMap);
                                //
                                logBarcode = "打勾";
                                logProductName = myList.get(position).get("cProductName");
                                logProductID = myList.get(position).get("ProductNo");
                                logQty = myList.get(position).get("Qty");
                                logNowQty = myList.get(position).get("NowQty");
                                logAdd = myList.get(position).get("Qty");
                                //
                                checkListArray();
                                adapter.notifyDataSetChanged();
                                checkBox.setChecked(false);
                                Log.e("checkBox","數量滿");

                            }else {
                                newMap = new LinkedHashMap<String, String>();
                                newMap.put("NowQty", String.valueOf(myList.get(position).get("Qty")));
                                newMap.put("ProductNo", myList.get(position).get("ProductNo"));
                                newMap.put("cProductName", myList.get(position).get("cProductName"));
                                newMap.put("Qty", myList.get(position).get("Qty"));
                                //myList.remove(position);
                                myList.set(position, newMap);
                                //
                                logBarcode = "打勾";
                                logProductName = myList.get(position).get("cProductName");
                                logProductID = myList.get(position).get("ProductNo");
                                logQty = myList.get(position).get("Qty");
                                logNowQty = myList.get(position).get("NowQty");
                                logAdd = myList.get(position).get("Qty");
                                //
                                checkListArray();
                                adapter.notifyDataSetChanged();
                                checkBox.setChecked(false);
                                Log.e("checkBox2",myList.get(position).get("NowQty"));
                                Log.e("checkBox3",myList.get(position).get("Qty"));
                            }

                        //有勾勾的時候點擊
                        } else {
                            if(myList.get(position).get("NowQty").equals(myList.get(position).get("Qty"))){
                                newMap = new LinkedHashMap<String, String>();
                                newMap.put("NowQty", "0");
                                newMap.put("ProductNo", myList.get(position).get("ProductNo"));
                                newMap.put("cProductName", myList.get(position).get("cProductName"));
                                newMap.put("Qty", myList.get(position).get("Qty"));
                                //myList.remove(position);
                                myList.set(position, newMap);
                                //
                                logBarcode = "清除";
                                logProductName = myList.get(position).get("cProductName");
                                logProductID = myList.get(position).get("ProductNo");
                                logQty = myList.get(position).get("Qty");
                                logNowQty = myList.get(position).get("NowQty");
                                logAdd = "-"+myList.get(position).get("Qty");
                                //
                                checkListArray();
                                adapter.notifyDataSetChanged();
                                checkBox.setChecked(false);
                                Log.e("checkBox","數量滿");

                            }else {
                                newMap = new LinkedHashMap<String, String>();
                                newMap.put("NowQty", String.valueOf(myList.get(position).get("Qty")));
                                newMap.put("ProductNo", myList.get(position).get("ProductNo"));
                                newMap.put("cProductName", myList.get(position).get("cProductName"));
                                newMap.put("Qty", myList.get(position).get("Qty"));
                                myList.set(position, newMap);
                                //
                                logBarcode = "清除";
                                logProductName = myList.get(position).get("cProductName");
                                logProductID = myList.get(position).get("ProductNo");
                                logQty = myList.get(position).get("Qty");
                                logNowQty = myList.get(position).get("NowQty");
                                logAdd =  "-"+myList.get(position).get("Qty");
                                //
                                checkListArray();
                                adapter.notifyDataSetChanged();
                                checkBox.setChecked(false);
                                Log.e("checkBox2",myList.get(position).get("NowQty"));
                                Log.e("checkBox3",myList.get(position).get("Qty"));
                            }

                        }

                        extelnalPrivateCreateFoler();
                    }

                });


            }

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
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShipperOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //POST後 回傳的JSON檔
                    //myDialog.dismiss();
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

                    myList = new ArrayList<LinkedHashMap<String, String>>();
                    myList2 = new ArrayList<LinkedHashMap<String, String>>();
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
                                cProductName = c.getString(c.getColumnIndex("cProductShortName"));  //商品名稱顯示改變
                                Log.e("cProductName", cProductName);
                            }
                            //用自訂類別 把JSONArray的值取出來

                            map = new LinkedHashMap<String, String>();
                            map.put("NowQty", String.valueOf(new NowQtyInfo(obj.optString("NowQty"))));
                            map.put("ProductNo", String.valueOf(new ProductIDInfo(obj.getString("ProductNo"))));
                            map.put("cProductName", String.valueOf(new ProductNameInfo(cProductName)));
                            map.put("Qty", String.valueOf(new QtyInfo(obj.getString("Qty"))));
                            map.put("check","0");
                            myList.add(map);
                            db.close();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listView = (ListView) findViewById(R.id.list);
                    checkListArray();
                    adapter = new SpecialAdapter(
                            ShipperOrderActivity.this,
                            myList,
                            lview4,
                            new String[]{"cProductName", "ProductNo", "Qty", "NowQty", "checkbox"},
                            new int[]{textView21, R.id.textView22, textView23, textView24, checkBox4});

                    //listView2 = (ListView) findViewById(R.id.list2);
                    checkListArray();
                    /*
                    adapter2 = new SpecialAdapter(
                            ShipperOrderActivity.this,
                            myList2,
                            lview4,
                            new String[]{"cProductName", "ProductNo", "Qty", "NowQty", "checkbox"},
                            new int[]{textView21, R.id.textView22, textView23, textView24, checkBox4});

                    */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myDialog.dismiss();
                            listView.setAdapter(adapter);

                            //若進入後 訂單數量和檢貨數量都已經檢滿 checkBox5 打勾勾
                            final CheckBox checkBox = (CheckBox) findViewById(checkBox5);
                            for (int i = 0; i<myList.size();i++){
                                if(Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))){
                                    checkInt++;
                                }
                            }
                            Log.e("checkInt", String.valueOf(checkInt));
                            if(checkInt==myList.size()){
                                checkBox.setChecked(true);
                            }else{
                                checkBox.setChecked(false);
                            }
                            //listView2.setAdapter(adapter2);
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


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
        iMatch=1;
        Btrans = new ArrayList();
        EditText editText = (EditText) findViewById(R.id.editText);

        String barcode = editText.getText().toString();
        logBarcode=barcode;
        Log.e("barcode", barcode);
        setBarcodeSQL();
        Cursor c = db4.query("tblTable4",                          // 資料表名字
                null,                                              // 要取出的欄位資料
                "cBarcode = ? OR cProductID = ?",                  // 查詢條件式(WHERE)
                new String[]{barcode,barcode},                     // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductIDeSQL = c.getString(c.getColumnIndex("cProductID"));
            logProductID = cProductIDeSQL;
            Log.e("cBarcode1", cProductIDeSQL);
            Btrans.add(cProductIDeSQL);

        }
        iCheck = c.getCount();
        Log.e("筆數", String.valueOf(iCheck));
        //條碼找不到商品編號
        if (iCheck == 0) {
            setThingSQL();
            Cursor c2 = db.query("tblTable",                            // 資料表名字
                    null,                                              // 要取出的欄位資料
                    "cProductID=?",                                    // 查詢條件式(WHERE)
                    new String[]{barcode},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                    null,                                              // Group By字串語法
                    null,                                              // Having字串法
                    null);                                             // Order By字串語法(排序)

            while (c2.moveToNext()) {
                cProductIDeSQL = c2.getString(c2.getColumnIndex("cProductID"));
                Log.e("cBarcode2", cProductIDeSQL);
                Btrans.add(cProductIDeSQL);
            }
            iCheck = c2.getCount();
            Log.e("筆數2", String.valueOf(iCheck));
            if(iCheck ==0 ){
                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                editText.setText("");
                logProductName="null";
                logQty = "null";
                logNowQty = "null";
                logAdd = "null";
                extelnalPrivateCreateFoler();
            }else if (iCheck == 1) {
                //先判斷條碼內的商品號碼是否有在listView裡
                if (checkID() == true) {
                    //Switch 關閉時
                    if (addInt == 1 && addNum ==0) {
                        //跳出輸入數字對話框
                        setAlertDialog();
                        Log.e("setAlertDialog","1");
                    } else if (addNum == 1) {
                        setNOWQty(1);
                    } else if (addNum == 5) {
                        setNOWQty(1);
                    } else if (addNum == 10) {
                        setNOWQty(1);
                    } else if (addNum == 999999) {
                        setNOWQty(1);
                    }else {
                        setNOWQty(1);
                    }
                } else {
                    Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                    logProductName="null";
                    logQty = "null";
                    logNowQty = "null";
                    logAdd = "null";
                    extelnalPrivateCreateFoler();
                }
                //條碼找到一筆以上商品編號
            } else if (iCheck > 1) {
                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                chooseThings();
                editText.setText("");
            }


            //條碼找到一筆商品編號
        } else if (iCheck == 1) {
            //先判斷條碼內的商品號碼是否有在listView裡
            if (checkID() == true) {
                //Switch 關閉時
                if (addInt == 1 && addNum == 0) {
                    //跳出輸入數字對話框
                    setAlertDialog();
                    Log.e("setAlertDialog","1");
                } else if (addNum == 1) {
                    setNOWQty(1);
                } else if (addNum == 5) {
                    setNOWQty(1);
                } else if (addNum == 10) {
                    setNOWQty(1);
                } else if (addNum == 999999) {
                    setNOWQty(1);
                }else {
                    setNOWQty(1);
                }
            } else {
                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                editText.setText("");
                logProductName="null";
                logQty = "null";
                logNowQty = "null";
                logAdd = "null";
                extelnalPrivateCreateFoler();
            }
            //條碼找到一筆以上商品編號
        } else if (iCheck > 1) {
            stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
            chooseThings();
            editText.setText("");
        }

    }

    //判斷條碼內的商品是否有在list裡 有就回傳true
    private boolean checkID() {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))) {
                return true;
            }

        }
        return false;
    }

    private boolean checkID2() {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (newStringArray[0].equals(myList.get(i3).get("ProductNo"))) {
                return true;
            }

        }
        return false;
    }

    private void setNOWQty(int getint2) {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("NowQty"));
                int i4 = Integer.parseInt(myList.get(i3).get("Qty"));

                Log.e("I22", String.valueOf(i2));
                Log.e("I44", String.valueOf(i4));
                //數量
                if (iMatch == 0) {
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                        vibrator ();
                        EditText editText = (EditText) findViewById(R.id.editText);
                        if (editText.getText().length() >= 13) {
                            editText.setText("");
                            editText.requestFocus();
                        }
                    } else {
                        if(i2 + getint2 <0){
                            i2=0;
                        }else{
                            i2 = i2 + getint2;
                        }
                            }
                } else  {
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                        vibrator ();

                        EditText editText = (EditText) findViewById(R.id.editText);
                        if (editText.getText().length() >= 13) {
                            editText.setText("");
                            editText.requestFocus();
                        }
                    } else  {
                        if(i2 + getint2 <0){
                            i2=0;
                        }else{
                            i2 = i2 + getint2;
                        }

                    }

                }


                Log.e("I2", String.valueOf(i2));
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", String.valueOf(i2));
                newMap.put("ProductNo", myList.get(i3).get("ProductNo"));
                newMap.put("cProductName", myList.get(i3).get("cProductName"));
                newMap.put("Qty", myList.get(i3).get("Qty"));
                myList.set(i3, newMap);
                //myList.remove(i).get("NowQty");
                //Log.e("myList",myList.remove(i).get("NowQty"));
                //
                logProductName = myList.get(i3).get("cProductName");
                logProductID = myList.get(i3).get("ProductNo");
                logNowQty = String.valueOf(i2);
                logQty = myList.get(i3).get("Qty");
                logAdd = String.valueOf(getint2);
                //
                checkListArray();
                adapter.notifyDataSetChanged();

                EditText editText = (EditText) findViewById(R.id.editText);
                if (editText.getText().length() >= 13) {
                    editText.setText("");
                    editText.requestFocus();
                }

            }
        }
        Bsound();
        extelnalPrivateCreateFoler();
    }

    private void setNOWQty2(int getint2) {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (newStringArray[0].equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("NowQty"));
                int i4 = Integer.parseInt(myList.get(i3).get("Qty"));

                Log.e("I22", String.valueOf(i2));
                Log.e("I44", String.valueOf(i4));
                //數量
                if (getint2 != 1) {
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                        vibrator ();
                        EditText editText = (EditText) findViewById(R.id.editText);
                        if (editText.getText().length() >= 13) {
                            editText.setText("");
                            editText.requestFocus();
                        }
                    } else {
                        if(i2 + getint2 <0){
                            i2=0;
                        }else{
                            i2 = i2 + getint2;
                        }
                    }
                } else {
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                        vibrator ();
                        EditText editText = (EditText) findViewById(R.id.editText);
                        if (editText.getText().length() >= 13) {
                            editText.setText("");
                            editText.requestFocus();
                        }
                    } else {
                        if(i2 + getint2 <0){
                            i2=0;
                        }else{
                            i2 = i2 + getint2;
                        }
                    }
                }



                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", String.valueOf(i2));
                newMap.put("ProductNo", myList.get(i3).get("ProductNo"));
                newMap.put("cProductName", myList.get(i3).get("cProductName"));
                newMap.put("Qty", myList.get(i3).get("Qty"));
                myList.set(i3, newMap);
                //
                logProductName = myList.get(i3).get("cProductName");
                logProductID = myList.get(i3).get("ProductNo");
                logNowQty = String.valueOf(i2);
                logQty = myList.get(i3).get("Qty");
                logAdd = String.valueOf(getint2);
                //
                checkListArray();
                adapter.notifyDataSetChanged();

                EditText editText = (EditText) findViewById(R.id.editText);
                if (editText.getText().length() >= 13) {
                    editText.setText("");
                    editText.requestFocus();
                }

            }

        }
        Bsound();
        extelnalPrivateCreateFoler();
    }
    private void addNOWQty() {
        if (checkID2() == true) {
            if (addNum == 0 && addInt == 1) {
                final View item = LayoutInflater.from(ShipperOrderActivity.this).inflate(R.layout.item, null);
                new AlertDialog.Builder(ShipperOrderActivity.this)
                        .setTitle("請輸入數量")
                        .setView(item)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText)findViewById(R.id.editText);
                                editText.setText("");
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.editText2);
                                if (editText.length() != 0) {
                                    getint = Integer.parseInt(editText.getText().toString());
                                    setNOWQty2(getint);
                                    EditText editText1 = (EditText)findViewById(R.id.editText);
                                    editText1.setText("");
                                }else {
                                    EditText editText1 = (EditText)findViewById(R.id.editText);
                                    editText1.setText("");
                                }
                            }
                        }).show();

            } else if (addNum == 1) {
                setNOWQty2(1);
            } else if (addNum == 5) {
                setNOWQty2(1);
            } else if (addNum == 10) {
                setNOWQty2(1);
            } else if (addNum == 999999) {
                setNOWQty2(1);
            }else {
                setNOWQty2(1);
            }
        } else {
            Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
            EditText editText = (EditText)findViewById(R.id.editText);
            editText.setText("");
            logProductName="null";
            logQty = "null";
            logNowQty = "null";
            logAdd = "null";
            extelnalPrivateCreateFoler();
        }

    }

    //按確定後 所執行
    public void enter(View v) {
        iMatch=1;
        cBarcode();
        checkListArray();
        adapter.notifyDataSetChanged();

    }

    //輸入的條碼 有兩個以上商品 跳出對話框 選擇商品
    private void chooseThings() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請選擇商品編號");
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("點擊", stringArray[i]);
                newStringArray[0] = stringArray[i];
                Log.e("點擊2", newStringArray[0]);
                Log.e("PRODUCTNO", map.get("ProductNo"));
                logProductID = newStringArray[0];
                addNOWQty();

            }
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //拍照按鍵 切換到拍照頁面 並把所需的資料傳遞過去
    public void onPicture(View v) {
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        EditText editText7 = (EditText)findViewById(R.id.editText7);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);
        frameLayout.setVisibility(View.GONE);

        LinTop.setVisibility(View.GONE);
        linear.setVisibility(View.GONE);
        linMid.setVisibility(View.GONE);
        linDown.setVisibility(View.GONE);
        editText7.setVisibility(View.GONE);
        list.setVisibility(View.GONE);

    }
    public void onBack (View v){
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        EditText editText7 = (EditText)findViewById(R.id.editText7);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);

        frameLayout.setVisibility(View.VISIBLE);
        LinTop.setVisibility(View.VISIBLE);
        linMid.setVisibility(View.VISIBLE);
        //linear.setVisibility(View.INVISIBLE);
        linDown.setVisibility(View.VISIBLE);
        editText7.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);
        if(PicADD ==1){
            LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
            Linear.setVisibility(View.VISIBLE);
        }else {
            LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
            Linear.setVisibility(View.GONE);
        }

        Allbase64 = new ArrayList();
        if(Abase64 != null){
            Allbase64.add("\"" + Abase64 + "\"");
        }
        if(Bbase64 != null){
            Allbase64.add("\"" + Bbase64 + "\"");
        }
        if(Cbase64 != null){
            Allbase64.add("\"" + Cbase64 + "\"");
        }
        if(Dbase64 != null){
            Allbase64.add("\"" + Dbase64 + "\"");
        }
        if(Ebase64 != null){
            Allbase64.add("\"" + Ebase64 + "\"");
        }


    }

    //從myList取出ProductNo NowQty 放入upList POST用
    private void AllBase64() {
        Map<String, String> upMap;
        upList = new ArrayList<LinkedHashMap<String, String>>();
        for (int i = 0; i < myList.size(); i++) {
            //LinkedHashMap<String, String>() 會依照put的順序
            upMap = new LinkedHashMap<String, String>();
            upMap.put("\"ProductNo\"", "\"" + myList.get(i).get("ProductNo") + "\"");
            upMap.put("\"NowQty\"", myList.get(i).get("NowQty"));
            upList.add((LinkedHashMap<String, String>) upMap);
        }
        Log.e("upList", String.valueOf(upList));
        String upString = String.valueOf(upList).replaceAll("=", ":");
        upStringList = upString.replaceAll(", ", ",");
        Log.e("upStringList", String.valueOf(upStringList));

    }

    // Uri 轉成Bitmap 再轉成 base64
    // bitmap 要轉成 jpg
    void AImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中
        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔

        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //調整存檔類別和檔案大小
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Abase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Abase64 + "\"");
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Bbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Bbase64 + "\"");
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Cbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Cbase64 + "\"");
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Dbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Dbase64 + "\"");
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Ebase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Ebase64 + "\"");
    }

    //動作按鍵
    public void onActivity(View v) {
        chooseActivity();
    }

    //動作按鍵的方法 (選擇檢貨或換人檢)
    private void chooseActivity() {
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        dialog_list.setTitle("動作");
        dialog_list.setItems(activity, new DialogInterface.OnClickListener() {
            @Override
            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Log.e("選取", activity[which]);
                Log.e("選取數字", String.valueOf(which));
                AllBase64();
                //換人檢
                if (which == 0) {
                    AllBase64();
                    PostChangeInfo post = new PostChangeInfo();
                    post.start();
                    setDialog();
                }
                //結案
                else if (which == 1) {
                    Log.e("MYLIST結案", String.valueOf(myList));
                    checkUP();
                    Log.e("Allbase64", String.valueOf(Allbase64));

                    if (check == 0) {
                        //setWait();
                        PostEndInfo post = new PostEndInfo();
                        post.start();
                        setDialog();
                    }else if (check!=0) {
                        Toast.makeText(ShipperOrderActivity.this, "商品未檢完", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        dialog_list.show();
    }
    //結案
    class PostEndInfo extends Thread {
        @Override
        public void run() {
            Log.e("Allbase642", String.valueOf(Allbase64));
            AllBase64();
            PostendInfo();
        }
    }

    //判斷是否有檢完
    private int checkUP() {
        check = 0;
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) != Integer.parseInt(myList.get(i).get("Qty"))) {
                check++;
                Log.e("NOWQTY", myList.get(i).get("NowQty"));
                Log.e("QTY", myList.get(i).get("Qty"));
                return check;
            }
        }
        return 1;
    }
    //結案 用OkHttp PostAPI
    private void PostendInfo() {
        Log.e("Allbase643", String.valueOf(Allbase64));
        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"PickupNumbers\" :\"" + checked + "\",\"PickupProducts\":" + upStringList + ",\"imgbase64\": " + Allbase64 + "}";
        Log.e("POST", json);
        RequestBody body = RequestBody.create(JSON, json);
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
                myDialog.dismiss();
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
                myDialog.dismiss();
                Log.e("結案後POST的回傳值", json);
                changeEnd(json);
                handler.sendEmptyMessage(0);
            }
        });
    }
    //換人檢
    class PostChangeInfo extends Thread {
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
        String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"PickupNumbers\" :\"" + checked + "\",\"PickupProducts\":" + upStringList + "}";
        Log.e("POST", json);
        RequestBody body = RequestBody.create(JSON, json);
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
                myDialog.dismiss();
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
                myDialog.dismiss();
                String json = response.body().string();
                Log.e("POST後的回傳值", json);
                changeEnd(json);
            }


        });
    }
    //POST成功後取得1跳回前一頁
    private void changeEnd(String json) {
        int result = 0;
        try {
            result = new JSONObject(json).getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 1) {
            back();
        }
    }
    //設定EditText 自動輸入
    private void setEditText() {
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(editText.getText().length()>=13){
                    cBarcode();
                    EditText editText1 = (EditText) findViewById(R.id.editText7);
                    editText1.getText();
                    editText1.requestFocus();

                }

                return false;
            }
        });


    }
    private void setEditText2() {
        final EditText editText = (EditText) findViewById(editText7);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                    EditText editText1 = (EditText) findViewById(R.id.editText);
                    editText1.getText();
                    editText1.requestFocus();
                return false;
            }

        });

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //d.dismiss();
            myDialog.dismiss();
        }
    };
    private void setWait(){
        d=new ProgressDialog(ShipperOrderActivity.this);
        d.setMessage("上傳中..");
        d.show();
    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME){ // 攔截HOME鍵
            return true;
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }
    //設定輸入數量框
    private void setAlertDialog(){
        final View item = LayoutInflater.from(ShipperOrderActivity.this).inflate(R.layout.item, null);
        new AlertDialog.Builder(ShipperOrderActivity.this)
                .setTitle("請輸入數量")
                .setView(item)
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //editText歸零
                        EditText editText = (EditText)findViewById(R.id.editText);
                        editText.setText("");
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) item.findViewById(R.id.editText2);
                        //如果有輸入數字 執行setNOWQty
                        if (editText.length() != 0) {
                            getint = Integer.parseInt(editText.getText().toString());
                            //判斷有無商品代碼 並帶入數字
                            setNOWQty(getint);
                            //editText歸零
                            EditText editText1 = (EditText)findViewById(R.id.editText);
                            editText1.setText("");
                            hideSystemNavigationBar();
                            View decorView = getWindow().getDecorView();
                            decorView.setOnSystemUiVisibilityChangeListener
                                    (new View.OnSystemUiVisibilityChangeListener() {
                                        @Override
                                        public void onSystemUiVisibilityChange(int visibility) {

                                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                                hideSystemNavigationBar();
                                            } else {
                                                // TODO: The system bars are NOT visible. Make any desired
                                                // adjustments to your UI, such as hiding the action bar or
                                                // other navigational controls.
                                                hideSystemNavigationBar();
                                            }
                                        }
                                    });
                        }else{
                            EditText editText1 = (EditText)findViewById(R.id.editText);
                            editText1.setText("");
                        }


                    }
                }).show();
        //editText歸零
        EditText editText1 = (EditText)findViewById(R.id.editText);
        editText1.setText("");
    }

    public void onPic1 (View v){
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 100);
        Log.e("imgUri", String.valueOf(imgUri));
    }
    public void onPic2 (View v){
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 101);
        Log.e("imgUri2", String.valueOf(imgUri));
    }
    public void onPic3 (View v){
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 102);
        Log.e("imgUri3", String.valueOf(imgUri));
    }
    public void onPic4 (View v){
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 103);
        Log.e("imgUri4", String.valueOf(imgUri));
    }
    public void onPic5 (View v){
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 104);
        Log.e("imgUri5", String.valueOf(imgUri));
    }
    //拍照後的預覽畫面設定
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode==100) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==101) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==102) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==103) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==104) {
            showImg(requestCode);
        }
        else {
            Toast.makeText(this, "沒有拍到照片1", Toast.LENGTH_LONG).show();

        }
    }


    void showImg(int requestCode) {

        Log.e("showing", String.valueOf(imgUri));
        if(imgUri!=null){
            BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
            option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
            BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中

            if(requestCode == 100){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView14);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                AImgUriBase64(imgUri);
            }else if(requestCode == 101){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView15);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                BImgUriBase64(imgUri);
            }else if(requestCode == 102){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView16);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                CImgUriBase64(imgUri);
            }else if(requestCode == 103){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView17);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                DImgUriBase64(imgUri);
            }else if(requestCode == 104){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView18);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                EImgUriBase64(imgUri);
            }
        }else{
            Toast.makeText(this, "沒有拍到照片2", Toast.LENGTH_LONG).show();

        }

    }
    public void onDelAll (View v){
        ImageView imv,imv2,imv3,imv4,imv5;
        imv = (ImageView) findViewById(R.id.imageView14);
        imv.setImageBitmap(null);
        imv2 = (ImageView) findViewById(R.id.imageView15);
        imv2.setImageBitmap(null);
        imv3 = (ImageView) findViewById(R.id.imageView16);
        imv3.setImageBitmap(null);
        imv4 = (ImageView) findViewById(R.id.imageView17);
        imv4.setImageBitmap(null);
        imv5 = (ImageView) findViewById(R.id.imageView18);
        imv5.setImageBitmap(null);
        Allbase64 = null;
    }
    public void onDel1 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView14);
        imv.setImageBitmap(null);
        Abase64 = null ;


    }
    public void onDel2 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView15);
        imv.setImageBitmap(null);
        Bbase64 = null ;
    }
    public void onDel3 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView16);
        imv.setImageBitmap(null);
        Cbase64 = null ;
    }
    public void onDel4 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView17);
        imv.setImageBitmap(null);
        Dbase64 = null ;
    }
    public void onDel5 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView18);
        imv.setImageBitmap(null);
        Ebase64 = null ;
    }


    public void onClickPic (View v){
        if(PicInt==0){
            LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
            LinTop.setVisibility(View.VISIBLE);
            if(PicADD ==1){
                LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
                Linear.setVisibility(View.VISIBLE);
            }else {
                LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
                Linear.setVisibility(View.GONE);
            }
            PicInt = 1;
        }else {
            LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
            LinTop.setVisibility(View.GONE);
            if(PicADD ==1){
                LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
                Linear.setVisibility(View.VISIBLE);
            }else {
                LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
                Linear.setVisibility(View.GONE);
            }
            PicInt = 0;
        }


    }
    public void onTest (View v){
        TestMyList();
    }
    private void TestMyList() {
        Log.e("查詢", String.valueOf(myList.indexOf(map)));

    }
    private void setDialog(){
        hideSystemNavigationBar();
        myDialog = new ProgressDialog(ShipperOrderActivity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }

    private void hideSystemNavigationBar() {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    protected void onResume() {
        hideSystemNavigationBar();
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {

                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            hideSystemNavigationBar();
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                            hideSystemNavigationBar();
                        }
                    }
                });
        super.onResume();
    }
    //檢完和沒檢完的排序
    private void checkListArray() {
        //先依照商品名稱排序
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("ProductNo"));
                String value2 = (o2.get("ProductNo"));

                return value1.compareTo(value2);
                //return value1.equals(value2);

            }

        });
        // 如果檢完貨 新添加的欄位(check)就等於商品名稱(方便下一次排序)
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("check", myList.get(i).get("ProductNo"));
                myList.set(i, newMap);
            } else {
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("check", "0");
                myList.set(i, newMap); // 替換
            }
        }
        //排序check (及撿完貨的排序)
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("check"));
                String value2 = (o2.get("check"));

                return value1.compareTo(value2);
                //return value1.equals(value2);

            }

        });

        /*
        for(int i2 = 0; i2 < myList.size(); i2++) {
            for (int i = 0; i < myList.size(); i++) {
                final LinkedHashMap<String, String> item = myList.get(i);
                int size = myList.size() - 1;
                Log.e("myList清單", String.valueOf(myList.get(i)));
                if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                    //Log.e("list", String.valueOf(myList.get(i)));
                    Log.e("item", String.valueOf(item));
                    //myList2.add(0,item);
                    myList.remove(i);

                    myList.add(size, item);

                } else {

                }
            }
        }
        */

        // 排序完後 檢完貨的位置 假如檢完 勾勾留著
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                mCheckSet.add(i);
            } else {
                mCheckSet.remove(i);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CheckBox checkBox = (CheckBox) findViewById(checkBox5);
                if(mCheckSet.size()==myList.size()){
                    checkBox.setChecked(true);
                }else {
                    checkBox.setChecked(false);
                }
            }
        });


    }
    private void setCheckBox (){
        final CheckBox checkBox = (CheckBox) findViewById(checkBox5);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    for (int i = 0;i<myList.size();i++){
                        newMap = new LinkedHashMap<String, String>();
                        newMap.put("NowQty", myList.get(i).get("Qty"));
                        newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                        newMap.put("cProductName", myList.get(i).get("cProductName"));
                        newMap.put("Qty", myList.get(i).get("Qty"));
                        newMap.put("check", myList.get(i).get("ProductNo"));
                        myList.set(i, newMap);
                        adapter.notifyDataSetChanged();
                        mCheckSet.add(i);
                        //若進入後 訂單數量和檢貨數量都已經檢滿 checkBox5 打勾勾

                    }
                    logBarcode = "全打勾";
                    logProductID = "全打勾";
                    logProductName = "全打勾";
                    logQty = "All";
                    logNowQty = "All";
                    logAdd = "All";

                }else {
                    Log.e("歸零","歸零");
                    for (int i = 0;i<myList.size();i++){
                        newMap = new LinkedHashMap<String, String>();
                        newMap.put("NowQty", "0");
                        newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                        newMap.put("cProductName", myList.get(i).get("cProductName"));
                        newMap.put("Qty", myList.get(i).get("Qty"));
                        newMap.put("check", "0");
                        myList.set(i, newMap);
                        adapter.notifyDataSetChanged();
                        mCheckSet.remove(i);
                        //若進入後 訂單數量和檢貨數量都已經檢滿 checkBox5 打勾勾

                    }
                    if(mCheckSet.size()==myList.size()){
                        checkBox.setChecked(true);
                    }else {
                        checkBox.setChecked(false);
                    }
                    logBarcode = "全清除";
                    logProductName = "全清除";
                    logProductID = "全清除";
                    logQty = "0";
                    logNowQty = "0";
                    logAdd = "0";
                }
                extelnalPrivateCreateFoler();
            }
        });
    }
    // 判斷是否要+1還是輸入數量 (NUM or +1 )
    public void onClickAdd (View v){
        if(addInt==1){
            addInt=0;
        }else {
            addInt=1;
        }
        if(addInt==0){
            Button button = (Button)findViewById(R.id.button20);
            button.setText("+1");
        }else {
            Button button = (Button)findViewById(R.id.button20);
            button.setText("NUM");
        }
    }
    //手機震動
    private void vibrator (){
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(1500);

    }
    //判斷外部儲存空間是否可以讀寫
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    //判斷外部空間是否可以儲存
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    //公用
    private File getExtermalStoragePublicDir(String albumName) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(file.mkdir()){
            File f = new File(file, albumName);
            if(f.mkdir()){
                return f;
            }
        }
        return new File(file, albumName);
    }
    //外部空間建立公開資料夾
    private void extelnalPublicCreateFoler(){
        String fileName = "APK";
        File dir = getExtermalStoragePublicDir("aa");
        File f = new File(dir.getPath(), fileName);
        String data = "時間:"+logToday+",條碼:"+logBarcode+",商品名稱:"+logProductID+",訂單數量:"+logQty+",檢貨數量:"+logNowQty+",增加數量:"+logAdd+"\r\n";

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //私用
    private File getExtermalStoragePrivateDir(String albumName) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            //Log.e("", "Directory not created or exist");
        }
        return file;
    }
    //外部空間建立私有資料夾
    private void extelnalPrivateCreateFoler(){
        time();
        File dir = getExtermalStoragePrivateDir("Log");
        String fileName = today2+".txt";
        File f = new File(dir, fileName);

        String data = "時間:"+logToday+",條碼:"+logBarcode+",商品名稱:"+logProductName+",商品編號:"+logProductID+",訂單數量:"+logQty+",檢貨數量:"+logNowQty+",增加數量:"+logAdd+"\r\n";

        try {
            //new FileOutputStream(f,true) 多加true 就可以複寫
            FileOutputStream outputStream = new FileOutputStream(f,true);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //得到現在時間
    private void time() {
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy/MM/dd/ HH:mm:ss";
        String dateformat2=  "yyyyMMdd";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        SimpleDateFormat df2 = new SimpleDateFormat(dateformat2);
        today = df.format(mCal.getTime());
        today2 = df2.format(mCal.getTime());
        logToday = today;
    }
    //音效
    private void Bsound(){
        mSoundPool.play(mSoundID,1.0F,1.0F,0,0,0.0f);
    }


}


