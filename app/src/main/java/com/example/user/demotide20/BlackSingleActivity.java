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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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

public class BlackSingleActivity extends AppCompatActivity {
    String url = "http://demo.shinda.com.tw/ModernWebApi/Blank.aspx";
    ArrayList mWarehouse,mInputType;
    Object Warehouse;
    Object InputType;
    String cUserName,cUserID,json,cProductName,cProductIDeSQL,order,upStringList,activity2,Sname,Sname2;
    SQLiteDatabase db, db4;
    Map<String, String> map;
    ArrayList<Map<String, String>> myList;
    ArrayList<Map<String, String>> upList;HashMap<String, String> addMap;
    ListView listView;
    int addNum = 0;
    SpecialAdapter adapter,adapter2;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    LinearLayout linear;
    final String DB_NAME = "tblTable";
    final String[] newStringArray = new String[1];
    Map<String, String> newMap;
    ArrayList Btrans,AllImgUri,Allbase64;
    int getint,indexSpinner;
    String[] stringArray;
    Uri AImgUri,BImgUri,CImgUri,DImgUri,EImgUri;
    String Abase64,Bbase64,Cbase64,Dbase64,Ebase64;
    ProgressDialog d;
    int index,index2,iCheck,iMatch=0;
    Uri imgUri;    //用來參照拍照存檔的 Uri 物件
    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_single);
        EditText editText = (EditText)findViewById(R.id.editText);
        //editText.requestFocus();
        toolBar();
        getPreviousPage();
        PostGetType post = new PostGetType();
        post.start();
        setSwitch();
        setArraylist();
        setEditText();
        setEditText2();

    }
    private void setArraylist(){
        if(activity2!=null&&activity2.equals("pictures")){
            listView = (ListView) findViewById(R.id.list);
            adapter = new BlackSingleActivity.SpecialAdapter(
                    BlackSingleActivity.this,
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
        }else{
            myList = new ArrayList<>();
            map = new HashMap<String, String>();
            map.put("NowQty","");
            map.put("ProductNo", "");
            map.put("cProductName", "");
            map.put("Qty", "");
            myList.add(map);
            Log.e("YES","YES");
            listView = (ListView) findViewById(R.id.list);
            adapter = new BlackSingleActivity.SpecialAdapter(
                    BlackSingleActivity.this,
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
        Intent intent = new Intent(BlackSingleActivity.this, AllListActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID",cUserID);
        bag.putInt("indexSpinner",indexSpinner);
        intent.putExtras(bag);
        startActivity(intent);
        BlackSingleActivity.this.finish();
    }
    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        index = bag.getInt("index");
        Log.e("index取得", String.valueOf(index));
        index2 = bag.getInt("index2");
        indexSpinner = bag.getInt("indexSpinner",0);
        AllImgUri = bag.getStringArrayList("AllImgUri");
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID",null);
        myList = (ArrayList<Map<String, String>>) bag.getSerializable("myList");
        activity2 = bag.getString("activity2",null);
        Log.e("空白接收myList", String.valueOf(myList));
        Log.e("cUserID",cUserID);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }
    //Post 取得Spinner的資料 並顯示出來
    class PostGetType extends Thread{
        @Override
        public void run() {
            PostGetTypeInfo();
        }
        //Post getType
        private void PostGetTypeInfo() {

            final OkHttpClient client = new OkHttpClient();
            //要上傳的內容(JSON)--帳號登入
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"gettype\"}";
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
                            Toast.makeText(BlackSingleActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //post 成功後執行
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //取得回傳資料json 還是JSON檔
                    String json = response.body().string();
                    Log.e("POST後的回傳值", json);
                    //解析 JSON
                    paraJson(json);

                }
            });
        }
        //解析JSON的方法 並把取出的資料放進arrayList
        private void paraJson(String json) {
            String BlankType;
            InputType = new JSONArray();
            Warehouse = new JSONArray();
            mInputType = new ArrayList<>();
            mWarehouse = new ArrayList<>();
            mInputType.add("入庫分類");
            mWarehouse.add("庫別");

            try {
                JSONObject j = new JSONObject(json);
                BlankType= j.getString("BlankType");
                JSONObject j2 = new JSONObject(BlankType);
                for(int i=0 ; i< j2.getJSONArray("Warehouse").length();i++){
                    Warehouse = j2.getJSONArray("Warehouse").getJSONObject(i).get("Name");
                    mWarehouse.add(Warehouse);

                }
                for(int i2 = 0; i2<j2.getJSONArray("InputType").length();i2++){
                    InputType = j2.getJSONArray("InputType").getJSONObject(i2).get("Name");
                    mInputType.add(InputType);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Warehouse", String.valueOf(mWarehouse));
            Log.e("InputType", String.valueOf(mInputType));
            //把資料放進Spinner
            mSpinner(mInputType,mWarehouse);
        }
        //放進Spinner的方法
        private void mSpinner(ArrayList mInputType, ArrayList mWarehouse) {
            final Spinner SInputType = (Spinner) findViewById(R.id.spinner3);
            final ArrayAdapter<String> list = new ArrayAdapter<>(
                    BlackSingleActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    mInputType);

            Log.e("Warehouse", String.valueOf(mWarehouse));
            Log.e("InputType", String.valueOf(BlackSingleActivity.this.mInputType));
            Log.e("index2222", String.valueOf(index));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(activity2!=null&&activity2.equals("pictures")) {
                        SInputType.setAdapter(list);
                        SInputType.setSelection(index);
                        list.notifyDataSetChanged();

                    }else {
                        SInputType.setAdapter(list);
                    }


                }
            });

            final Spinner SWarehouse = (Spinner) findViewById(R.id.spinner2);
            final ArrayAdapter<String> list2 = new ArrayAdapter<>(
                    BlackSingleActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    mWarehouse);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(activity2!=null&&activity2.equals("pictures")) {
                        SWarehouse.setAdapter(list2);
                        SWarehouse.setSelection(index2);
                        list2.notifyDataSetChanged();

                    }else{
                        SWarehouse.setAdapter(list2);
                    }

                }
            });

            //spinner 點擊事件
            SInputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //所點擊的索引值
                    index = SInputType.getSelectedItemPosition();
                    //所點擊的內容文字
                    Sname = SInputType.getSelectedItem().toString();
                    Log.e("index", String.valueOf(index));
                    Log.e("name", Sname);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // sometimes you need nothing here
                }
            });
            //spinner 點擊事件
            SWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //所點擊的索引值
                    index2 = SWarehouse.getSelectedItemPosition();
                    //所點擊的內容文字
                    Sname2 = SWarehouse.getSelectedItem().toString();
                    Log.e("index", String.valueOf(index2));
                    Log.e("name", Sname2);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // sometimes you need nothing here
                }
            });

        }

    }
    public void enter (View v){
        cBarcode();
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
        addNum = 9999;
        if (iMatch==1){
            addNum=9998;
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
    private void cBarcode() {
        iMatch=1;
        Btrans = new ArrayList();
        EditText editText = (EditText) findViewById(R.id.editText);
        String barcode = editText.getText().toString();
        Log.e("barcode", barcode);
        setBarcodeSQL();
        Cursor c = db4.query("tblTable4",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                "cBarcode = ? OR cProductID = ?",                                    // 查詢條件式(WHERE)
                new String[]{barcode,barcode},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
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
                        setAlertDialog();
                    } else if (addNum == 1) {
                        setNOWQty(1);
                    } else if (addNum == 5) {
                        setNOWQty(1);
                    } else if (addNum == 10) {
                        setNOWQty(1);
                    } else if (addNum == 9999) {
                        setNOWQty(1);
                    }else {
                        setNOWQty(1);
                    }
                } else {
                    //Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                    Log.e("cProductIDeSQL!!", cProductIDeSQL);
                    setThingSQL();
                    c = db.query("tblTable",                            // 資料表名字
                            null,                                              // 要取出的欄位資料
                            "cProductID=?",                                    // 查詢條件式(WHERE)
                            new String[]{cProductIDeSQL},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                            null,                                              // Group By字串語法
                            null,                                              // Having字串法
                            null);                                             // Order By字串語法(排序)

                    while (c.moveToNext()) {
                        cProductName = c.getString(c.getColumnIndex("cProductName"));
                        Log.e("cProductName", cProductName);
                        addMap = new HashMap<String, String>();
                        addMap.put("cProductName", cProductName);
                        addMap.put("ProductNo", cProductIDeSQL);
                        addMap.put("Qty", "0");
                        addMap.put("NowQty","");
                        myList.add(addMap);
                        adapter.notifyDataSetChanged();

                        Log.e("myList", String.valueOf(myList));
                    }
                    //跳出輸入數字對話框
                    setAlertDialog();
                }

                //條碼找到一筆以上商品編號
            } else if (iCheck > 1) {
                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                chooseThings();
            }

        } else if (iCheck == 1) {
            //先判斷條碼內的商品號碼是否有在listView裡
            if (checkID() == true) {
                //Switch 關閉時
                if (addNum == 0) {
                   setAlertDialog();
                } else if (addNum == 1) {
                    setNOWQty(1);
                } else if (addNum == 5) {
                    setNOWQty(1);
                } else if (addNum == 10) {
                    setNOWQty(1);
                } else if (addNum == 9999) {
                    setNOWQty(1);
                }else {
                    setNOWQty(1);
                }
            } else {
                //Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                Log.e("cProductIDeSQL!!", cProductIDeSQL);
                setThingSQL();
                c = db.query("tblTable",                            // 資料表名字
                        null,                                              // 要取出的欄位資料
                        "cProductID=?",                                    // 查詢條件式(WHERE)
                        new String[]{cProductIDeSQL},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                        null,                                              // Group By字串語法
                        null,                                              // Having字串法
                        null);                                             // Order By字串語法(排序)

                while (c.moveToNext()) {
                    cProductName = c.getString(c.getColumnIndex("cProductName"));
                    Log.e("cProductName", cProductName);
                    addMap = new HashMap<String, String>();
                    addMap.put("cProductName", cProductName);
                    addMap.put("ProductNo", cProductIDeSQL);
                    addMap.put("Qty", "0");
                    addMap.put("NowQty","");
                    myList.add(addMap);
                    adapter.notifyDataSetChanged();

                    Log.e("myList", String.valueOf(myList));
                }
                //跳出輸入數字對話框
                setAlertDialog();
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
            if(cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))){
                return true;
            }
            Log.e("CHECK",myList.get(i3).get("ProductNo"));
        }
        return false;
    }
    //判斷條碼內的商品是否有在list裡 有就回傳true (兩個以上商品編號)
    private boolean checkID2(){
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if(newStringArray[0].equals(myList.get(i3).get("ProductNo"))){
                return true;
            }

        }
        return false;
    }
    //增加數量的方法
    private void setNOWQty(int getint2){
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (cProductIDeSQL.equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("Qty"));
                //int i4 = Integer.parseInt(myList.get(i3).get("Qty"));
                Log.e("I22", String.valueOf(i2));
                //數量
                if(getint2!=1){
                    i2 = i2 + getint2;
                }else{
                    i2 = i2 + getint2;
                }


                Log.e("I2", String.valueOf(i2));
                newMap = new HashMap<String, String>();
                newMap.put("NowQty", myList.get(i3).get("NowQty"));
                newMap.put("ProductNo", myList.get(i3).get("ProductNo"));
                newMap.put("cProductName", myList.get(i3).get("cProductName"));
                newMap.put("Qty", String.valueOf(i2));
                myList.set(i3, newMap);
                //myList.remove(i).get("NowQty");
                //Log.e("myList",myList.remove(i).get("NowQty"));
                adapter.notifyDataSetChanged();
                Log.e("MYLISTTT", String.valueOf(myList));
                EditText editText = (EditText)findViewById(R.id.editText);
                editText.setText("");
            }
        }
    }
    //增加數量的方法(兩個以上商品編號)
    private void setNOWQty2(int getint2){
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if (newStringArray[0].equals(myList.get(i3).get("ProductNo"))) {
                int i2 = Integer.parseInt(myList.get(i3).get("Qty"));
                Log.e("I22", String.valueOf(i2));
                //數量
                if(getint2!=1){
                        i2 = i2 + getint2;
                }else{
                    i2 = i2 + getint2;
                }


                Log.e("I2", String.valueOf(i2));
                newMap = new HashMap<String, String>();
                newMap.put("NowQty", myList.get(i3).get("NowQty"));
                newMap.put("ProductNo", myList.get(i3).get("ProductNo"));
                newMap.put("cProductName", myList.get(i3).get("cProductName"));
                newMap.put("Qty", String.valueOf(i2));
                myList.set(i3, newMap);
                //myList.remove(i).get("NowQty");
                //Log.e("myList",myList.remove(i).get("NowQty"));
                adapter.notifyDataSetChanged();
                Log.e("MYLISTTT", String.valueOf(myList));
                EditText editText = (EditText)findViewById(R.id.editText);
                editText.setText("");
            }
        }
    }
    //增加數量(兩個以上商品編號)
    private void addNOWQty(){
        if(checkID2()==true){
            if (addNum == 0) {
                //跳出輸入數字對話框
                final View item = LayoutInflater.from(BlackSingleActivity.this).inflate(R.layout.item, null);
                new AlertDialog.Builder(BlackSingleActivity.this)
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
                                //如果有輸入數字 執行setNOWQty
                                if (editText.length() != 0) {
                                    getint = Integer.parseInt(editText.getText().toString());
                                    //判斷有無商品代碼 並帶入數字
                                    setNOWQty2(getint);
                                    EditText editText1 = (EditText)findViewById(R.id.editText);
                                    editText1.setText("");
                                }else{
                                    EditText editText1 = (EditText)findViewById(R.id.editText);
                                    editText1.setText("");
                                }


                            }
                        }).show();

            }else if(addNum==1){
                setNOWQty2(1);
            }else if(addNum==5){
                setNOWQty2(1);
            }else if(addNum==10) {
                setNOWQty2(1);
            }else if(addNum==999999) {
                setNOWQty2(1);
            }else {
                setNOWQty2(1);
            }
        }else{
            //Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
            Log.e("cProductIDeSQL!!", newStringArray[0]);
            setThingSQL();
            Cursor c = db.query("tblTable",                            // 資料表名字
                    null,                                              // 要取出的欄位資料
                    "cProductID=?",                                    // 查詢條件式(WHERE)
                    new String[]{newStringArray[0]},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                    null,                                              // Group By字串語法
                    null,                                              // Having字串法
                    null);                                             // Order By字串語法(排序)

            while (c.moveToNext()) {
                cProductName = c.getString(c.getColumnIndex("cProductName"));
                Log.e("cProductName", cProductName);
                addMap = new HashMap<String, String>();
                addMap.put("cProductName", cProductName);
                addMap.put("ProductNo", newStringArray[0]);
                addMap.put("Qty", "1");
                addMap.put("NowQty","");
                myList.add(addMap);
                adapter.notifyDataSetChanged();

                Log.e("myList", String.valueOf(myList));
            }
            //跳出輸入數字對話框
            final View item = LayoutInflater.from(BlackSingleActivity.this).inflate(R.layout.item, null);
            new AlertDialog.Builder(BlackSingleActivity.this)
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
                            //如果有輸入數字 執行setNOWQty
                            if (editText.length() != 0) {
                                getint = Integer.parseInt(editText.getText().toString());
                                //判斷有無商品代碼 並帶入數字
                                setNOWQty2(getint);
                                EditText editText1 = (EditText)findViewById(R.id.editText);
                                editText1.setText("");
                            }else{
                                EditText editText1 = (EditText)findViewById(R.id.editText);
                                editText1.setText("");
                            }


                        }
                    }).show();
        }

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
                //Log.e("PRODUCTNO",map.get("ProductNo"));
                addNOWQty();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

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
    public void onPicture (View v){
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);

        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);
        frameLayout.setVisibility(View.GONE);

        LinTop.setVisibility(View.GONE);
        linear.setVisibility(View.GONE);
        linMid.setVisibility(View.GONE);
        linDown.setVisibility(View.GONE);

        list.setVisibility(View.GONE);
    }

    public void onBack (View v){
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        EditText editText10 = (EditText)findViewById(R.id.editText10);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);

        frameLayout.setVisibility(View.VISIBLE);
        LinTop.setVisibility(View.VISIBLE);
        linMid.setVisibility(View.VISIBLE);
        linear.setVisibility(View.INVISIBLE);
        linDown.setVisibility(View.VISIBLE);

        list.setVisibility(View.VISIBLE);


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
    private void AllBase64() {
        Log.e("AllImgUri", String.valueOf(AllImgUri));

        Map<String, String> upMap;

        upList = new ArrayList<Map<String, String>>();
        for(int i=1; i < myList.size(); i++){
            //LinkedHashMap<String, String>() 會依照put的順序
            upMap = new LinkedHashMap<String, String>();
            upMap.put("\"ProductNo\"","\"" +myList.get(i).get("ProductNo")+ "\"" );
            upMap.put("\"Qty\"", myList.get(i).get("Qty"));
            upList.add(upMap);
        }
        Log.e("upList", String.valueOf(upList));
        String upString = String.valueOf(upList).replaceAll("=", ":");
        upStringList = upString.replaceAll(", ",",");
        Log.e("upStringList", String.valueOf(upStringList));

    }

    // Uri 轉成Bitmap 再轉成 base64
    // bitmap 要轉成 jpg 然後上傳時要給提示
    void AImgUriBase64(Uri uri) {

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔

        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream );
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Bbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Bbase64",Bbase64);
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Cbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Cbase64",Cbase64);
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Dbase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Dbase64",Dbase64);
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        Ebase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Ebase64",Ebase64);
        //Allbase64.add("\"" +Ebase64+"\"");
    }
    //執行動作按鈕
    public void onActivity(View v){
        AllBase64();
        Log.e("upList", String.valueOf(upList));
        Log.e("upList.size()", String.valueOf(upList.size()));
        if(upList.size()>0&&index>0&&index2>0){
            setWait();

            PostEndInfo post = new PostEndInfo();
            post.start();
        }else {
            Toast.makeText(BlackSingleActivity.this, "資料填寫不完整", Toast.LENGTH_SHORT).show();
        }


    }
    //結案
    class PostEndInfo extends Thread{
        @Override
        public void run() {
            AllBase64();
            PostendInfo();
        }
    }
    //結案 用OkHttp PostAPI
    private void PostendInfo() {
        Log.e("Allbase64", String.valueOf(Allbase64));
        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"BlankInfo\":{\"BlankType\":"+index+",\"WHType\":"+index2+",\"UserID\":\""+cUserID+"\",\"BlankProduct\":"+upStringList+"},\"imgbase64\": "+Allbase64+"}";
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
                        Toast.makeText(BlackSingleActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
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
    //設定EditText 自動輸入
    private void setEditText() {
        final EditText editText = (EditText) findViewById(R.id.editText);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(editText.getText().length()>=13){

                    cBarcode();
                }

                return false;
            }
        });


    }
    private void setEditText2() {
        final EditText editText = (EditText) findViewById(R.id.editText9);
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
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            d.dismiss();
        }
    };
    private void setWait(){
        d=new ProgressDialog(BlackSingleActivity.this);
        d.setMessage("上傳中..");
        d.show();
    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(BlackSingleActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return super.onKeyDown(keyCode, event);
    }
    //設定輸入數量框
    private void setAlertDialog(){
        final View item = LayoutInflater.from(BlackSingleActivity.this).inflate(R.layout.item, null);
        new AlertDialog.Builder(BlackSingleActivity.this)
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
}
