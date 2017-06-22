package com.example.user.demotide20;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StorageOrderActivity extends AppCompatActivity {
    String cUserName, cUserID, mLackNO, mLackName, cProductName, lackNoAdd, lackNameAdd,cProductIDeSQL,upStringList;
    String url = "http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";
    ArrayList<Map<String, String>> myList,saveList;
    ArrayList upList,Btrans,delList;
    int iMax = 0;
    int newCount = 0;
    int editCount = 0;
    int indexSpinner;
    Map<String, String> map;
    Map<String, String> upMap;
    Map<String, String> checkMap;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    ListView listView;
    SimpleAdapter adapter;
    final String DB_NAME = "tblTable";
    Map<String, String> newMap,addMap;
    String[] stringArray;
    final String[] newStringArray = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_order);
        EditText editText = (EditText)findViewById(R.id.editText8);
        //editText.requestFocus();
        toolBar();
        getPreviousPage();
        Post post = new Post();
        post.start();
        setEditText();
        setEditText2();
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
        //因為cUserName從上一頁傳過來了 所以要回到上一頁 要把cUserName再傳回去
        Intent intent = new Intent(StorageOrderActivity.this, StorageActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID", cUserID);
        bag.putInt("indexSpinner",indexSpinner);
        intent.putExtras(bag);
        startActivity(intent);
        StorageOrderActivity.this.finish();
    }

    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        mLackNO = bag.getString("mLackNO", null);
        mLackName = bag.getString("mLackName", null);
        indexSpinner = bag.getInt("indexSpinner",0);
        Log.e("cUserID", cUserID);

        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
        TextView textView1 = (TextView) findViewById(R.id.textView24);
        textView1.setText(mLackNO);
        TextView textView2 = (TextView) findViewById(R.id.textView7);
        textView2.setText(mLackName);
    }
    //取得儲位清單
    class Post extends Thread {
        @Override
        public void run() {
            PostLackList();
        }
        //Post儲位清單
        private void PostLackList() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"detail\",\"LackNo\":\"" + mLackNO + "\"}";
            Log.e("JSON", json);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Log.e("UP", body.toString());
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("取得list清單的網址", response.toString());
                    Log.e("取得的list清單", json);

                    try {
                        JSONObject j = new JSONObject(json);
                        JSONArray array = j.getJSONArray("LackList");
                        Log.e("取出LackList", String.valueOf(array));
                        JSONArray array1 = array.getJSONObject(0).getJSONArray("LackProduct");
                        Log.e("取出LackProduct", String.valueOf(array1));
                        //解析回傳JSON
                        parseJson(String.valueOf(array1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
        //解析回傳JSON
        private void parseJson(String array1) {
            myList = new ArrayList<Map<String, String>>();
            try {
                final JSONArray array = new JSONArray(array1);
                for (iMax = 0; iMax < array.length(); iMax++) {
                    JSONObject obj = array.getJSONObject(iMax);
                    //開啟資料庫 用ProductNo比對SQL的cProductID
                    setThingSQL();
                    Cursor c = db.query("tblTable",                            // 資料表名字
                            null,                                              // 要取出的欄位資料
                            "cProductID=?",                                    // 查詢條件式(WHERE)
                            new String[]{obj.optString("ProductID")},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                            null,                                              // Group By字串語法
                            null,                                              // Having字串法
                            null);                                             // Order By字串語法(排序)

                    while (c.moveToNext()) {
                        cProductName = c.getString(c.getColumnIndex("cProductName"));
                        Log.e("cProductName", cProductName);
                    }

                    map = new LinkedHashMap<String, String>();
                    map.put("cProductName", cProductName);
                    map.put("ProductID", obj.optString("ProductID"));
                    map.put("Count", obj.optString("Count"));

                    myList.add(map);
                }
                Log.e("mylist", String.valueOf(myList));
                setLackListView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //商品條碼SQL
    private void setBarcodeSQL() {
        helper4 = new MyDBhelper4(this, "tblTable4", null, 1);
        db4 = helper4.getWritableDatabase();
    }

    //商品清單SQL
    private void setThingSQL() {
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        db = helper.getWritableDatabase();
    }
    //listView 顯示 點擊
    private void setLackListView() {
        listView = (ListView) findViewById(R.id.list);
        upList = new ArrayList();
        delList = new ArrayList();
        checkMap = new LinkedHashMap();
        adapter = new SimpleAdapter(
                StorageOrderActivity.this,
                myList,
                R.layout.lview6,
                new String[]{"cProductName", "ProductID", "Count", "checkbox"},
                new int[]{R.id.textView34, R.id.textView33, R.id.textView32, R.id.checkBox2}) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                //获取相应的view中的checkbox对象
                if (convertView == null)
                    convertView = View.inflate(StorageOrderActivity.this, R.layout.lview6, null);
                final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox2);

                checkBox.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (((CheckBox) v).isChecked()) {
                            upList.add(String.valueOf(position));

                        } else {
                            upList.remove(String.valueOf(position));
                        }
                        Log.e("UPLIST", String.valueOf(upList));

                    }
                });
                return super.getView(position, convertView, parent);
            }

        };

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });

    }
    public void onDel(View v){
        Log.e("UPLIST2", String.valueOf(upList));
        ArrayList dlist;
        dlist = new ArrayList();
        for(int i=0; i<upList.size();i++){
            int i2 = Integer.valueOf((String) upList.get(i)) ;
            dlist.add(myList.get(i2));
        }
        for(int i=0; i<dlist.size();i++){
            for(int i2 = 0,len=myList.size();i2<len;i2++){
                if(myList.get(i2).equals(dlist.get(i))){
                    myList.remove(i2);
                    i2--;
                    len--;

                }
            }
        }
        setLackListView();
    }
    //設定 用來新增數量
    public void onAdd(View v) {
        listAdd();
    }
    //新增數量的方法
    private void listAdd() {
        Btrans = new ArrayList();
        EditText edit = (EditText) findViewById(R.id.editText3);
        String editList = edit.getText().toString();
        final EditText edit2 = (EditText) findViewById(R.id.editText8);

        if (edit2.length() != 0) {
            editCount = Integer.parseInt(edit2.getText().toString());
            Log.e("edit2", editList);
            Log.e("editCount", String.valueOf(editCount));
            setBarcodeSQL();
            Cursor c = db4.query("tblTable4",                            // 資料表名字
                    null,                                              // 要取出的欄位資料
                    "cBarcode = ? OR cProductID = ?",                                    // 查詢條件式(WHERE)
                    new String[]{editList,editList},                           // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                    null,                                              // Group By字串語法
                    null,                                              // Having字串法
                    null);                                             // Order By字串語法(排序)

            while (c.moveToNext()) {
                cProductIDeSQL = c.getString(c.getColumnIndex("cProductID"));
                Log.e("cProductIDeSQL", cProductIDeSQL);
                //對話框顯示出 有重複的條碼商品編號
                Btrans.add(cProductIDeSQL);

            }
            int i = c.getCount();
            Log.e("筆數", String.valueOf(i));
            //條碼找不到商品編號
            if (i == 0) {


                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                EditText editText = (EditText)findViewById(R.id.editText3);
                editText.setText("");
            } else if (i == 1) {
                //先判斷條碼內的商品號碼是否有在listView裡
                if (checkID() == true) {
                    //有在listView裡 把新增的數量加入
                    for (int i2 = 0; i2 < myList.size(); i2++) {
                        if (cProductIDeSQL.equals(myList.get(i2).get("ProductID"))) {

                            newCount = editCount+Integer.parseInt(myList.get(i2).get("Count"));
                            Log.e("count",myList.get(i2).get("Count"));
                            Log.e("editCount", String.valueOf(editCount));
                            newMap = new LinkedHashMap<String, String>();
                            newMap.put("ProductID", myList.get(i2).get("ProductID"));
                            newMap.put("Count", String.valueOf(newCount));
                            newMap.put("cProductName",myList.get(i2).get("cProductName"));
                            myList.set(i2, newMap);
                            adapter.notifyDataSetChanged();
                            EditText editText = (EditText)findViewById(R.id.editText3);
                            editText.setText("");
                        }
                    }
                }else {
                        //Toast.makeText(this, "請輸入正確商品條碼", Toast.LENGTH_SHORT).show();
                    Log.e("cProductIDeSQL!!",cProductIDeSQL);
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
                        addMap = new LinkedHashMap<String, String>();
                        addMap.put("cProductName",cProductName);
                        addMap.put("ProductID",cProductIDeSQL);
                        addMap.put("Count", String.valueOf(editCount));
                        myList.add(addMap);
                        adapter.notifyDataSetChanged();
                        EditText editText = (EditText)findViewById(R.id.editText3);
                        editText.setText("");
                        Log.e("addMap", String.valueOf(myList));
                    }

                }

            }else if (i>1){
                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                //Log.e("stringArray", String.valueOf(stringArray));
                chooseThings();
            }
        }else {
            Toast.makeText(this, "請輸入數量", Toast.LENGTH_SHORT).show();
        }
    }
    //判斷輸入商品是否有在list
    private boolean checkID() {
        for (int i3 = 0; i3 < myList.size(); i3++) {
            if(cProductIDeSQL.equals(myList.get(i3).get("ProductID"))){
                return true;
            }
        }
        return false;
    }
    //判斷條碼內的商品是否有在list裡 有就回傳true (兩個以上商品編號)
    private boolean checkID2(){
        for (int i2 = 0; i2 < myList.size(); i2++) {
            if(newStringArray[0].equals(myList.get(i2).get("ProductID"))){
                return true;
            }
        }
        return false;
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
                //Log.e("點擊2",newStringArray[0]);
                //Log.e("PRODUCTID",map.get("ProductID"));
                addNOWQty();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private void addNOWQty(){
        if(checkID2()==true){
            for (int i2 = 0; i2 < myList.size(); i2++) {
                if (newStringArray[0].equals(myList.get(i2).get("ProductID"))) {
                    newCount = editCount+Integer.parseInt(myList.get(i2).get("Count"));
                    newMap = new LinkedHashMap<String, String>();
                    newMap.put("ProductID", myList.get(i2).get("ProductID"));
                    newMap.put("Count", String.valueOf(newCount));
                    newMap.put("cProductName",myList.get(i2).get("cProductName"));
                    myList.set(i2, newMap);

                    adapter.notifyDataSetChanged();
                }
            }
        } else{
            //Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
            Log.e("cProductIDeSQL!!!",newStringArray[0]);
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
                addMap = new LinkedHashMap<String, String>();
                addMap.put("cProductName",cProductName);
                addMap.put("ProductID",newStringArray[0]);
                addMap.put("Count", String.valueOf(editCount));
                myList.add(addMap);
                adapter.notifyDataSetChanged();

                Log.e("addMap", String.valueOf(myList));
            }
        }

    }

    public void onSave (View v){
        saveList = new ArrayList<>();
        for(int i=0; i < myList.size(); i++){
            //LinkedHashMap<String, String>() 會依照put的順序
            upMap = new LinkedHashMap<String, String>();
            upMap.put("\"ProductID\"","\"" +myList.get(i).get("ProductID")+ "\"" );
            upMap.put("\"Count\"", myList.get(i).get("Count"));
            saveList.add(upMap);

        }
        String upString = String.valueOf(saveList).replaceAll("=", ":");
        upStringList = upString.replaceAll(", ",",");
        Log.e("upStringList", String.valueOf(upStringList));
        ListSave listSave = new ListSave();
        listSave.start();

    }
    class ListSave extends Thread {
        @Override
        public void run() {

            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\n" +
                    "  \"Token\": \"\",\n" +
                    "  \"Action\": \"update\",\n" +
                    "  \"UserID\": \"test\",\n" +
                    "  \"LackInfo\": {\n" +
                    "    \"LackNo\": \"" + mLackNO + "\",\n" +
                    "    \"LackName\": \"" + mLackName + "\",\n" +
                    "    \"LackProduct\": " + upStringList + "\n" +
                    "  }\n" +
                    "}";
            Log.e("JSONIIII", json);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("OkHttp2", json);
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
            if(result==1){
                back();
            }
        }

    }
    private void setEditText() {
        final EditText editText = (EditText) findViewById(R.id.editText3);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(editText.getText().length()>=13){
                    listAdd();
                    //EditText editText1 = (EditText)findViewById(R.id.editText14);
                    //editText1.requestFocus();
                }

                return false;
            }
        });


    }

    private void setEditText2() {
        final EditText editText = (EditText) findViewById(R.id.editText14);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText editText1 = (EditText) findViewById(R.id.editText3);
                editText1.getText();
                editText1.requestFocus();
                return false;
            }

        });


    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(StorageOrderActivity.this)
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

}
