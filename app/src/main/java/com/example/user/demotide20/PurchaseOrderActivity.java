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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class PurchaseOrderActivity extends AppCompatActivity {
    String cUserName, cUserID, json, cProductName, cProductIDeSQL, order, upStringList, activity2, order2;
    SQLiteDatabase db, db4;
    String url = "http://demo.shinda.com.tw/ModernWebApi/Purchase.aspx";
    Map<String, String> map;
    ArrayList<Map<String, String>> myList, upList;
    ListView listView;
    int addNum = 0, iMax = 0, check = 0, indexSpinner;
    SpecialAdapter adapter;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    LinearLayout linear;
    final String DB_NAME = "tblTable";
    final String[] newStringArray = new String[1];
    Map<String, String> newMap;
    ArrayList Btrans, AllImgUri, Allbase64;
    int getint;
    String[] stringArray;
    Uri AImgUri, BImgUri, CImgUri, DImgUri, EImgUri;
    String Abase64, Bbase64, Cbase64, Dbase64, Ebase64;
    final String[] activity = {"換人檢", "結案"};
    ProgressDialog pd;
    int iCheck, iMatch = 0;
    Uri imgUri;    //用來參照拍照存檔的 Uri 物件
    Bitmap bmp;

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

    ProgressDialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order);
        EditText editText = (EditText) findViewById(R.id.editText);
        //editText.requestFocus();
        toolBar();
        getPreviousPage();

        Post post = new Post();
        post.start();
        setEditText();
        setEditText2();

        setSwitch();
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

    private void back() {
        Intent intent = new Intent(PurchaseOrderActivity.this, PurchaseActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID", cUserID);
        bag.putInt("indexSpinner", indexSpinner);
        intent.putExtras(bag);
        startActivity(intent);
        PurchaseOrderActivity.this.finish();
    }

    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        myList = (ArrayList<Map<String, String>>) getIntent().getSerializableExtra("myList");
        Log.e("myList拍照回傳至採購單", String.valueOf(myList));
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        AllImgUri = bag.getStringArrayList("AllImgUri");
        activity2 = bag.getString("activity2");
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        order = bag.getString("order", null);
        order2 = bag.getString("order2", null);
        indexSpinner = bag.getInt("indexSpinner", 0);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
        TextView textView1 = (TextView) findViewById(R.id.textView11);
        textView1.setText(order);
    }

    class Post extends Thread {
        @Override
        public void run() {
            //POST取得採購清單
            PostThingListInfo();
        }

        private void PostThingListInfo() {
            //把連接到的採購單帶入JSON並POST上去
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json3 = "{\"Token\":\"\" ,\"Action\":\"dopurchase\",\"UserID\" :\"" + cUserID + "\" ,\"PurchaseID\" : \"" + order2 + "\"}";
            Log.e("JSON", json3);
            RequestBody body = RequestBody.create(JSON, json3);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PurchaseOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //取得POST上去後所得到的JSON檔
                    json = response.body().string();
                    Log.e("採購單明細", json);
                    String json2 = null;
                    try {
                        JSONObject j = new JSONObject(json);
                        json2 = j.getString("PurchaseProducts");

                        Log.e("取出PickUpProducts", json2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    parseJson(json2);
                }
            });
        }

    }

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
                    Log.e("cProductName", cProductName);
                }
                //用自訂類別 把JSONArray的值取出來
                map = new HashMap<String, String>();
                map.put("NowQty", String.valueOf(new NowQtyInfo(obj.optString("NowQty"))));
                map.put("ProductNo", String.valueOf(new ProductIDInfo(obj.getString("ProductNo"))));
                map.put("cProductName", String.valueOf(new ProductNameInfo(cProductName)));
                map.put("Qty", String.valueOf(new PurchaseOrderActivity.QtyInfo(obj.getString("Qty"))));
                myList.add(map);
                db.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.list);
        checkListArray();
        adapter = new SpecialAdapter(
                PurchaseOrderActivity.this,
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

    //改變listView(SimpleAdapter) item的顏色
    //#=0x30
    public class SpecialAdapter extends SimpleAdapter {
        private int[] colors = new int[]{0x30ffffff, 0x30696969};
        private int colors2 = Color.BLUE;

        public SpecialAdapter(Context context, ArrayList<Map<String, String>> items, int resource, String[] from, int[] to) {
            super(context, items, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
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
            }

            return view;
        }
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
        if (iCheck == 1) {
            setNOWQty(addNum);
        } else if (iCheck > 1) {
            setNOWQty2(addNum);
        }
        iMatch = 0;
    }

    public void add5(View v) {
        addNum = 5;
        if (iMatch == 1) {
            addNum = 4;
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
        addNum = 9999;
        if (iMatch == 1) {
            addNum = 9998;
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

    private void cBarcode() {
        iMatch = 1;
        Btrans = new ArrayList();
        EditText editText = (EditText) findViewById(R.id.editText);
        String barcode = editText.getText().toString();
        Log.e("barcode", barcode);
        setBarcodeSQL();
        Cursor c = db4.query("tblTable4",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                "cBarcode = ? OR cProductID = ?",                                    // 查詢條件式(WHERE)
                new String[]{barcode, barcode},                            // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductIDeSQL = c.getString(c.getColumnIndex("cProductID"));
            Log.e("cBarcode", cProductIDeSQL);
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
            }else if (iCheck == 1) {
                //先判斷條碼內的商品號碼是否有在listView裡
                if (checkID() == true) {
                    //Switch 關閉時
                    if (addNum == 0) {
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
                if (addNum == 0) {
                    //跳出輸入數字對話框
                    setAlertDialog();
                } else if (addNum == 1) {
                    setNOWQty(1);
                } else if (addNum == 5) {
                    setNOWQty(1);
                } else if (addNum == 10) {
                    setNOWQty(1);
                } else if (addNum == 999999) {
                    setNOWQty(1);
                } else {
                    setNOWQty(1);
                }
            } else {
                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                editText.setText("");
            }
            //條碼找到一筆以上商品編號
        } else if (iCheck > 1) {
            stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
            chooseThings();
        }
    }

    //判斷條碼內的商品是否有在list裡 有就回傳true
    private boolean checkID() {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))) {
                return true;
            }
            Log.e("CHECK", myList.get(i3).get("ProductNo"));
        }
        return false;
    }

    //判斷條碼內的商品是否有在list裡 有就回傳true (兩個以上商品編號)
    private boolean checkID2() {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (newStringArray[0].equals(myList.get(i3).get("ProductNo"))) {
                return true;
            }

        }
        return false;
    }

    //增加數量的方法
    private void setNOWQty(int getint2) {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("NowQty"));
                int i4 = Integer.parseInt(myList.get(i3).get("Qty"));
                Log.e("I22", String.valueOf(i2));
                Log.e("I44", String.valueOf(i4));
                //數量
                if (getint2 != 1) {
                    if (i2 + getint2 > i4 || getint2 > i4 || i2 > i4) {
                        i2 = i4;
                        Toast.makeText(PurchaseOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PurchaseOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                    } else {
                        if(i2 + getint2 <0){
                            i2=0;
                        }else{
                            i2 = i2 + getint2;
                        }
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
                checkListArray();
                adapter.notifyDataSetChanged();
                Log.e("MYLISTTT", String.valueOf(myList));
                EditText editText = (EditText) findViewById(R.id.editText);
                editText.setText("");
            }
        }
    }

    //增加數量的方法(兩個以上商品編號)
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
                        Toast.makeText(PurchaseOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PurchaseOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                    } else {
                        if(i2 + getint2 <0){
                            i2=0;
                        }else{
                            i2 = i2 + getint2;
                        }
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
                checkListArray();
                adapter.notifyDataSetChanged();
                EditText editText = (EditText) findViewById(R.id.editText);
                if (editText.getText().length() >= 13) {
                    editText.setText("");
                    editText.requestFocus();
                }
            }
        }
    }

    //增加數量(兩個以上商品編號)
    private void addNOWQty() {
        if (checkID2() == true) {
            if (addNum == 0) {
                final View item = LayoutInflater.from(PurchaseOrderActivity.this).inflate(R.layout.item, null);
                new AlertDialog.Builder(PurchaseOrderActivity.this)
                        .setTitle("請輸入數量")
                        .setView(item)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) findViewById(R.id.editText);
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
                                    EditText editText1 = (EditText) findViewById(R.id.editText);
                                    editText1.setText("");
                                } else {
                                    EditText editText1 = (EditText) findViewById(R.id.editText);
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
            } else {
                setNOWQty2(1);
            }
        } else {
            Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText("");

        }

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
                addNOWQty();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //按確定後 所執行
    public void enter(View v) {
        iMatch = 1;
        cBarcode();

    }

    //拍照按鍵 切換到拍照頁面 並把所需的資料傳遞過去
    public void onPicture(View v) {
        LinearLayout LinTop = (LinearLayout) findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout) findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout) findViewById(R.id.linDown);
        EditText editText10 = (EditText) findViewById(R.id.editText10);
        ListView list = (ListView) findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        frameLayout.setVisibility(View.GONE);

        LinTop.setVisibility(View.GONE);
        linear.setVisibility(View.GONE);
        linMid.setVisibility(View.GONE);
        linDown.setVisibility(View.GONE);
        editText10.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
    }

    public void onBack(View v) {
        LinearLayout LinTop = (LinearLayout) findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout) findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout) findViewById(R.id.linDown);
        EditText editText10 = (EditText) findViewById(R.id.editText10);
        ListView list = (ListView) findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.FrameLayout);

        frameLayout.setVisibility(View.VISIBLE);
        LinTop.setVisibility(View.VISIBLE);
        linMid.setVisibility(View.VISIBLE);
        linear.setVisibility(View.INVISIBLE);
        linDown.setVisibility(View.VISIBLE);
        editText10.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);


        Allbase64 = new ArrayList();
        if (Abase64 != null) {
            Allbase64.add("\"" + Abase64 + "\"");
        }
        if (Bbase64 != null) {
            Allbase64.add("\"" + Bbase64 + "\"");
        }
        if (Cbase64 != null) {
            Allbase64.add("\"" + Cbase64 + "\"");
        }
        if (Dbase64 != null) {
            Allbase64.add("\"" + Dbase64 + "\"");
        }
        if (Ebase64 != null) {
            Allbase64.add("\"" + Ebase64 + "\"");
        }


    }

    //從myList取出ProductNo NowQty 放入upList POST用
    private void AllBase64() {
        Log.e("AllImgUri", String.valueOf(AllImgUri));

        Map<String, String> upMap;

        upList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < myList.size(); i++) {
            //LinkedHashMap<String, String>() 會依照put的順序
            upMap = new LinkedHashMap<String, String>();
            upMap.put("\"ProductNo\"", "\"" + myList.get(i).get("ProductNo") + "\"");
            upMap.put("\"NowQty\"", myList.get(i).get("NowQty"));
            upList.add(upMap);
        }
        Log.e("upList", String.valueOf(upList));
        String upString = String.valueOf(upList).replaceAll("=", ":");
        upStringList = upString.replaceAll(", ", ",");
        Log.e("upStringList", String.valueOf(upStringList));

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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Abase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64

        //Allbase64.add("\"" +Abase64+"\"");
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
        Log.e("Bbase64", Bbase64);
        //Allbase64.add("\"" +Bbase64+"\"");
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
        Log.e("Cbase64", Cbase64);
        //Allbase64.add("\"" +Cbase64+"\"");
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
        Log.e("Dbase64", Dbase64);
        //Allbase64.add("\"" +Dbase64+"\"");
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
        Log.e("Ebase64", Ebase64);
        //Allbase64.add("\"" +Ebase64+"\"");
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
                //換人檢
                if (which == 0) {
                    AllBase64();
                    PostChangeInfo post = new PostChangeInfo();
                    post.start();

                }
                //結案
                else if (which == 1) {

                    Log.e("MYLIST結案", String.valueOf(myList));
                    checkUP();
                    Log.e("CHECKUP", String.valueOf(check));
                    if (check == 0) {
                        setWait();
                        PostEndInfo post = new PostEndInfo();
                        post.start();

                    } else if (check != 0) {
                        Toast.makeText(PurchaseOrderActivity.this, "商品未檢完", Toast.LENGTH_SHORT).show();
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

            AllBase64();
            PostendInfo();
        }
    }

    //結案方法 用OkHttp PostAPI
    private void PostendInfo() {


        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"PurchaseID\" :\"" + order2 + "\",\"PurchaseProducts\":" + upStringList + ",\"imgbase64\": " + Allbase64 + "}";
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PurchaseOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                Log.e("結案後POST的回傳值", json);
                changeEnd(json);
                handler.sendEmptyMessage(0);
            }
        });
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

    //換人檢
    class PostChangeInfo extends Thread {
        @Override
        public void run() {
            PostChangeInfo();
        }
    }

    //換人檢方法 用OkHttp PostAPI
    private void PostChangeInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"PurchaseID\" :\"" + order2 + "\",\"PurchaseProducts\":" + upStringList + "}";
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PurchaseOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                Log.e("換人檢POST後的回傳值", json);
                changeEnd(json);
            }

        });
    }

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
                if (editText.getText().length() >= 13) {
                    cBarcode();
                }

                return false;
            }
        });


    }

    private void setEditText2() {
        final EditText editText = (EditText) findViewById(R.id.editText10);
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

    //儲存
    class PostSaveInfo extends Thread {
        @Override
        public void run() {
            PostSaveInfo();
        }
    }

    private void PostSaveInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"savenotchageuser\",\"PurchaseID\" :\"" + order2 + "\",\"PurchaseProducts\":" + upStringList + "}";
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PurchaseOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                Log.e("換人檢POST後的回傳值", json);

            }

        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            d.dismiss();
        }
    };

    private void setWait() {
        d = new ProgressDialog(PurchaseOrderActivity.this);
        d.setMessage("上傳中..");
        d.show();
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

    //設定輸入數量框
    private void setAlertDialog() {
        final View item = LayoutInflater.from(PurchaseOrderActivity.this).inflate(R.layout.item, null);
        new AlertDialog.Builder(PurchaseOrderActivity.this)
                .setTitle("請輸入數量")
                .setView(item)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //editText歸零
                        EditText editText = (EditText) findViewById(R.id.editText);
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
                            EditText editText1 = (EditText) findViewById(R.id.editText);
                            editText1.setText("");
                        } else {
                            EditText editText1 = (EditText) findViewById(R.id.editText);
                            editText1.setText("");
                        }


                    }
                }).show();
        //editText歸零
        EditText editText1 = (EditText) findViewById(R.id.editText);
        editText1.setText("");
    }

    public void onPic1(View v) {
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 100);
        Log.e("imgUri", String.valueOf(imgUri));
    }

    public void onPic2(View v) {
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 101);
        Log.e("imgUri2", String.valueOf(imgUri));
    }

    public void onPic3(View v) {
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 102);
        Log.e("imgUri3", String.valueOf(imgUri));
    }

    public void onPic4(View v) {
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 103);
        Log.e("imgUri4", String.valueOf(imgUri));
    }

    public void onPic5(View v) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            showImg(requestCode);
        } else if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            showImg(requestCode);
        } else if (resultCode == Activity.RESULT_OK && requestCode == 102) {
            showImg(requestCode);
        } else if (resultCode == Activity.RESULT_OK && requestCode == 103) {
            showImg(requestCode);
        } else if (resultCode == Activity.RESULT_OK && requestCode == 104) {
            showImg(requestCode);
        } else {
            Toast.makeText(this, "沒有拍到照片1", Toast.LENGTH_LONG).show();

        }
    }


    void showImg(int requestCode) {

        Log.e("showing", String.valueOf(imgUri));
        if (imgUri != null) {
            BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
            option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
            BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中

            if (requestCode == 100) {
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView14);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                AImgUriBase64(imgUri);
            } else if (requestCode == 101) {
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView15);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                BImgUriBase64(imgUri);
            } else if (requestCode == 102) {
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView16);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                CImgUriBase64(imgUri);
            } else if (requestCode == 103) {
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView17);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                DImgUriBase64(imgUri);
            } else if (requestCode == 104) {
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView18);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                EImgUriBase64(imgUri);
            }
        } else {
            Toast.makeText(this, "沒有拍到照片2", Toast.LENGTH_LONG).show();

        }

    }

    public void onDelAll(View v) {
        ImageView imv, imv2, imv3, imv4, imv5;
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

    public void onDel1(View v) {
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView14);
        imv.setImageBitmap(null);
        Abase64 = null;


    }

    public void onDel2(View v) {
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView15);
        imv.setImageBitmap(null);
        Bbase64 = null;
    }

    public void onDel3(View v) {
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView16);
        imv.setImageBitmap(null);
        Cbase64 = null;
    }

    public void onDel4(View v) {
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView17);
        imv.setImageBitmap(null);
        Dbase64 = null;
    }

    public void onDel5(View v) {
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView18);
        imv.setImageBitmap(null);
        Ebase64 = null;
    }

    //把撿完的貨物移到最下面
    //要修改
    private void checkListArray() {
        for (int i2 = 0; i2 < myList.size(); i2++) {
            for (int i = 0; i < myList.size(); i++) {
                Map<String, String> item = myList.get(i);
                int size = myList.size() - 1;
                Log.e("myList", String.valueOf(myList.get(i)));
                if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                    //Log.e("list", String.valueOf(myList.get(i)));
                    Log.e("item", String.valueOf(item));
                    myList.remove(i);
                    myList.add(size, item);
                    //adapter.notifyDataSetChanged();
                }
            }
        }

    }

    private void cBarcode22() {
        iMatch = 1;
        Btrans = new ArrayList();
        EditText editText = (EditText) findViewById(R.id.editText);
        String barcode = editText.getText().toString();
        Log.e("barcode", barcode);
        setBarcodeSQL();
        Cursor c = db4.query("tblTable4",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                null,                                               // 查詢條件式(WHERE)
                null,                                               // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductIDeSQL = c.getString(c.getColumnIndex("cProductID"));
            Log.e("cBarcode22", cProductIDeSQL);
            Btrans.add(cProductIDeSQL);

        }
    }
}