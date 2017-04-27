package com.example.user.demotide20;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class ShipperActivity extends AppCompatActivity {
    String cUserName,listname,listTotal,json4,door1=null,cUserID;
    String name,order ;
    String checked2,checked3;
    int index ;
    ArrayList<String> checked;
    ArrayList<String> json2;

    //檢貨單 客戶API
    String url = "http://demo.shinda.com.tw/ModernWebApi/Pickup.aspx";
    public class ProductInfo {
        private String cCustomerName;
        private String cTotal;


        //建構子
        ProductInfo(final String CustomerName, final String Total) {
            this.cCustomerName = CustomerName;
            this.cTotal = Total;


        }
        //方法
        @Override
        public String toString() {
            return this.cCustomerName + "("+ this.cTotal +")";
        }
    }
    public class PickNOInfo {
        private String cPickupNo;
        private String cTotal;


        //建構子
        PickNOInfo(final String PickupNo, final String Total) {
            this.cPickupNo = PickupNo;
            this.cTotal = Total;


        }
        //方法
        @Override
        public String toString() {
            return this.cPickupNo + "("+ this.cTotal +")";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper);
        getPreviousPage();
        toolBar();
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
                Intent intent = new Intent(ShipperActivity.this, AllListActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID",cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                ShipperActivity.this.finish();
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
        cUserID = bag.getString("cUserID",null);
        Log.e("cUserID",cUserID);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }

    // 執行緒 - 執行PostCustomerInfo方法
    class Post extends Thread{
        @Override
        public void run() {
            //POST後取得客戶清單
            PostCustomerInfo();
        }

        private void PostCustomerInfo() {
            //post--取撿貨單客戶列表
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"customer\",\"UserID\":\""+cUserID+"\"}";
            Log.e("JSON",json);
            RequestBody body = RequestBody.create(JSON,json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            okhttp3.Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("客戶API回傳JSON",json);
                    //解析 回傳JSON
                    //{"result":"1","PickUpCustomers":[{"CustomerID":"C000000003","CustomerName":"磯法資訊","Total":19500.00},{"CustomerID":"C000000002","CustomerName":"新達科技","Total":13100.00},{"CustomerID":"C000000001","CustomerName":"大島屋企業","Total":9400.00}]}
                    json2 = new ArrayList<>();
                    try {
                        JSONObject j = new JSONObject(json);
                        for(int i=0;i<j.getJSONArray("PickUpCustomers").length();i++){
                            String json0 = j.getJSONArray("PickUpCustomers").getString(i);
                            json2.add(json0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //[{"CustomerID":"C000000003","CustomerName":"磯法資訊","Total":19500}, {"CustomerID":"C000000002","CustomerName":"新達科技","Total":13100}, {"CustomerID":"C000000001","CustomerName":"大島屋企業","Total":9400}]
                    Log.e("客戶API回傳JSON解析", String.valueOf(json2));
                    //parseJson(String.valueOf(json2));
                    parseJson(String.valueOf(json2));
                }
                //POST成功後回傳的值(陣列)取出來 用spinner顯示
                private void parseJson(final String json2) {
                    //取值
                    try {
                        //建立一個ArrayList
                        final ArrayList trans = new ArrayList<>();
                        //建立一個JSONArray 並把POST回傳資料json(JSON檔)帶入
                        JSONArray array = new JSONArray(json2);
                        //ArrayList 新增 請選擇這一單項
                        trans.add("請選擇");
                        //用迴圈取出JSONArray內的JSONObject標題為"CustomerName"的值
                        //用迴圈取出JSONArray內的JSONObject標題為"Total"的值
                        //另設一字串把"CustomerName"的值和"Total"的值帶入
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            //String id = obj.getString("cCustomerID");
                            trans.add(new ProductInfo(obj.optString("CustomerName"), obj.optString("Total")));
                            //String listname = obj.getString("CustomerName");
                            //String listTotal = obj.getString("Total");
                            //String newTotal = listname+"("+listTotal+")";
                            Log.e("客戶清單和金額", String.valueOf(trans));
                            //ArrayList 新增JSONObject標題為"newTotal"的值
                            //顯示在Spinner的資訊


                        }

                        //宣告並取得Spinner
                        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
                        //設定Spinner
                        final ArrayAdapter list = new ArrayAdapter<>(
                                ShipperActivity.this,
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
                                name = spinner.getSelectedItem().toString();
                                //index=0 為請選擇 所以不能用 ProductInfo
                                if(index !=0){
                                    ProductInfo cName = (ProductInfo) list.getItem(index) ;
                                    order = cName.cCustomerName;
                                    Log.e("客戶名稱",order );
                                }

                                Log.e("客戶清單點擊 索引值", String.valueOf(index));
                                Log.e("客戶清單點擊 內容", name);
                                //點擊後所要執行的方法 並把所回傳的json和索引值帶入
                                postjson2(String.valueOf(json2), index);

                            }

                            //點擊 spinner項目後 所要執行的方法
                            private void postjson2(String json2, int index) {

                                try {
                                    //把點到的索引值-1(多了請選擇) 就能連結到所點到的json2的客戶ID
                                    door1 = new JSONArray(json2).getJSONObject(index - 1).getString("CustomerID");
                                    Log.e("Spnner點選後的客戶ID", door1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //把連接到的客戶IP帶入JSON並POST上去
                                OkHttpClient client = new OkHttpClient();
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");
                                //取客戶有的撿貨單
                                String json = "{\"Token\":\"\" ,\"Action\":\"pickups\",\"UserID\":\""+cUserID+"\",\"CustomerID\":\""+door1+"\"}";
                                Log.e("取客戶有的撿貨單", json);
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
                                        //取得POST上去後所得到的JSON檔
                                        //{"result":"1","CustomerPickups":[{"PickupNo":"S20160000004","Total":300.00},{"PickupNo":"S20160000014","Total":2000.00}]}
                                        String json3 = response.body().string();
                                        Log.e("取客戶有的撿貨單回傳", json3);
                                        //取出CustomerPickups
                                        //[{"PickupNo":"S20160000004","Total":300},{"PickupNo":"S20160000014","Total":2000}]
                                        try {
                                            JSONObject j = new JSONObject(json3);
                                            json4 = j.getString("CustomerPickups");
                                            Log.e("取CustomerPickups",json4);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        parseJson2(json4);
                                    }
                                });
                            }
                            //POST成功後把回傳的值(陣列)取出來 用listView顯示 把JSON2帶進來
                            private void parseJson2(String json4) {
                                try {
                                    final ArrayList trans = new ArrayList<>();
                                    final JSONArray array = new JSONArray(json4);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        trans.add(new PickNOInfo(obj.optString("PickupNo"), obj.optString("Total")));
                                        Log.e("單號和總數量", String.valueOf(trans));
                                    }
                                    final ListView listView = (ListView) findViewById(R.id.listView);
                                    // 設定 ListView 選擇的方式 :
                                    // 單選 : ListView.CHOICE_MODE_SINGLE
                                    // 多選 : ListView.CHOICE_MODE_MULTIPLE
                                    listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                                    // 陣列接收器
                                    // RadioButton Layout 樣式 : android.R.layout.simple_list_item_single_choice
                                    // CheckBox Layout 樣式    : android.R.layout.simple_list_item_multiple_choice
                                    // trans 是陣列
                                    final ArrayAdapter<PickNOInfo> list = new ArrayAdapter<>(
                                            ShipperActivity.this,
                                            android.R.layout.simple_list_item_multiple_choice,
                                            trans);
                                    //非主執行緒顯示UI
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //顯示出listView
                                            listView.setVisibility(View.VISIBLE);
                                            //設定 ListView 的接收器, 做為選項的來源
                                            listView.setAdapter(list);
                                            //假如選到請選擇 list將不會出現
                                            if (index ==0){
                                                listView.setVisibility(View.GONE);
                                            }

                                        }
                                    });
                                    //ListView的點擊方法
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                            AbsListView list = (AbsListView)adapterView;
                                            Adapter adapter = list.getAdapter();
                                            SparseBooleanArray array = list.getCheckedItemPositions();
                                            checked = new ArrayList<>(list.getCheckedItemCount());

                                            for (int i = 0; i < array.size(); i++) {
                                                int key = array.keyAt(i);
                                                if (array.get(key)) {
                                                    //超重要 只取出項目裡的cPickupNO
                                                    PickNOInfo checkNO = (PickNOInfo)adapter.getItem(key);
                                                    checked.add(checkNO.cPickupNo);
                                                    Log.e("被點擊到的listView", String.valueOf(checked));
                                                }

                                            }

                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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

        if(!(checked == null || index == 0)){

            //點擊後到下一頁和所要傳的資料
            //BUG 最後一個會有殘留值
            //把逗號的空白處取代
            checked2 = String.valueOf(checked).replaceAll(", ", ",");
            int i = checked2.length();
            //再取字串範圍 (0和最後是[])
            //回傳指定範圍(1.i-1)第二個和倒數第二個
            checked3 = checked2.substring(1, i - 1);
            Log.e("CHECK3",checked3);
            Intent intent = new Intent(ShipperActivity.this,ShipperOrderActivity.class);
            Bundle bag = new Bundle();
            bag.putString("checked", checked3);
            bag.putString("order", String.valueOf(order));
            bag.putString("cUserID",cUserID);
            bag.putString("cUserName",cUserName);
            intent.putExtras(bag);
            startActivity(intent);
            ShipperActivity.this.finish();

        }
        //沒有點擊
        else{
            Toast.makeText(ShipperActivity.this,"請選擇出貨單", Toast.LENGTH_SHORT).show();

        }



    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(ShipperActivity.this)
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
