package com.example.user.demotide20;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PurchaseActivity extends AppCompatActivity {
    String cUserName,cUserID;
    String order;
    String url = "http://demo.shinda.com.tw/ModernWebApi/Purchase.aspx";
    ArrayList<String> json2;
    int index;
    public class ProductIDInfo{
        private String mPurchaseNo;
        private String mSupplier;

        ProductIDInfo(String PurchaseNo,String Supplier ){
            this.mPurchaseNo = PurchaseNo;
            this.mSupplier = Supplier;
        }
        public String toString(){
            return mPurchaseNo+"("+mSupplier+")";
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        toolBar();
        getPreviousPage();
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
                Intent intent = new Intent(PurchaseActivity.this, AllListActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID", cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                PurchaseActivity.this.finish();
            }
        });
    }

    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        Log.e("cUserID", cUserID);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }

    class Post extends Thread {
        @Override
        public void run() {
            //POST後取得採購單
            PostThingInfo();
        }

        private void PostThingInfo() {
            //post--客戶
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"purchases\",\"UserID\" :\""+cUserID+"\"}";
            Log.e("JSON", json);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Log.e("UP2", body.toString());
            //使用OkHttp的newCall方法建立一個呼叫物件(尚未連線至主機)
            Call call = client.newCall(request);
            //呼叫call類別的enqueue進行排程連線(連線至主機)
            call.enqueue(new Callback() {
                //post 失敗後
                @Override
                public void onFailure(Call call, IOException e) {

                }

                //POST 成功後
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //取得回傳資料json JSON檔陣列
                    //[{"cCustomerID":"C000000001","cCustomerName":"大島屋企業"},{"cCustomerID":"C000000002","cCustomerName":"新達科技"},{"cCustomerID":"C000000003","cCustomerName":"磯法資訊"}]
                    String json = response.body().string();
                    Log.e("取得POST後的回傳值", json);
                    json2 = new ArrayList<String>();
                    try {
                        JSONObject j = new JSONObject(json);
                        for(int i=0;i<j.getJSONArray("UserPurchases").length();i++){
                            String json0 = j.getJSONArray("UserPurchases").getString(i);
                            Log.e("PurchaseNo",json0);
                            json2.add(json0);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    parseJson(String.valueOf(json2));
                    Log.e("JSON222", String.valueOf(json2));
                }

                private void parseJson(final String json2) {
                    //取值
                    try {

                        //建立一個ArrayList
                        final ArrayList trans = new ArrayList();
                        //建立一個JSONArray 並把POST回傳資料json(JSOM檔)帶入
                        JSONArray array = new JSONArray(json2);
                        //ArrayList 新增"請選擇"這一單項
                        trans.add("請選擇採購單");
                        //用迴圈取出JSONArray內的JSONObject標題為"PurchaseNo"的值
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            //String id = obj.getString("cCustomerID");

                            //String listname = obj.getString("PurchaseNo");
                            //String Supplier = obj.getString("Supplier");
                            //String AddString =listname + "("+Supplier+")";
                            //Log.e("PurchaseNo", AddString);
                            //ArrayList 新增JSONObject標題為"cCustomerName"的值
                            trans.add(new ProductIDInfo(obj.getString("PurchaseNo"),obj.getString("Supplier")));

                        }

                        //宣告並取得Spinner
                        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
                        //設定Spinner
                        final ArrayAdapter<String> list = new ArrayAdapter<>(
                                PurchaseActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                trans);

                        //顯示Spinner 非主執行緒的UI 需用runOnUiThread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setAdapter(list);
                            }
                        });

                        //spinner 點擊事件
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //所點擊的索引值
                                index = spinner.getSelectedItemPosition();
                                //所點擊的內容文字

                                order = spinner.getSelectedItem().toString();

                                Log.e("index", String.valueOf(index));
                                Log.e("name", String.valueOf(order));

                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // sometimes you need nothing here
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });

        }
    }
    public void enter (View v){
        int idx = order.indexOf("(");
        Log.e("idx", String.valueOf(idx));
        String order2 =order.substring(0, idx);
        Log.e("order2", order2);
        Intent intent = new Intent(PurchaseActivity.this, PurchaseOrderActivity.class);
        Bundle bag = new Bundle();
        bag.putString("order", String.valueOf(order));
        bag.putString("order2",order2);
        bag.putString("cUserName",cUserName);
        bag.putString("cUserID",cUserID);
        intent.putExtras(bag);
        startActivity(intent);
        PurchaseActivity.this.finish();
    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(PurchaseActivity.this)
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
