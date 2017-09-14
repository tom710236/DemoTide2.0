package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShipperActivity extends AppCompatActivity {
    String cUserName, listname, listTotal, json4, door1 = null, cUserID;
    String name, order;
    String checked2, checked3;
    int index, indexSpinner;
    ArrayList<String> checked;
    ArrayList<String> json2;
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> myList;
    Map<String, String> map;
    ArrayList upList;
    ProgressDialog myDialog, myDialog2;
    private HashSet<Integer> mCheckSet = new HashSet<Integer>();
    //檢貨單 客戶API
    //String url = "http://demo.shinda.com.tw/ModernWebApi/Pickup.aspx";
    //String url = "192.168.0.2:8011/Pickup.aspx";
    String url = Application.TideUrl+"Pickup.aspx";
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
            return this.cCustomerName + "(" + this.cTotal + ")";
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
            return this.cPickupNo + "(" + this.cTotal + ")";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper);
        getPreviousPage();
        toolBar();
        setDialog();
        Post post = new Post();
        post.start();
        setCheckBox();

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
                bag.putString("cUserID", cUserID);
                bag.putInt("indexSpinner", indexSpinner);
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
        cUserID = bag.getString("cUserID", null);
        indexSpinner = bag.getInt("indexSpinner", 0);
        Log.e("cUserID", cUserID);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }

    // 執行緒 - 執行PostCustomerInfo方法
    class Post extends Thread {

        @Override
        public void run() {
            if(isConnected()){
                //POST後取得客戶清單
                PostCustomerInfo();

            }else {
                Toast.makeText(ShipperActivity.this,"請確認網路是否正常",Toast.LENGTH_SHORT).show();
                myDialog2.dismiss();
            }

        }

        private void PostCustomerInfo() {

            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"customer\",\"UserID\":\"" + cUserID + "\"}";
            Log.e("JSON", json);
            final RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            okhttp3.Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //非主執行緒顯示UI(Toast)
                    myDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShipperActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    myDialog.dismiss();
                    //Log.e("客戶API回傳JSON", json);
                    //解析 回傳JSON
                    //{"result":"1","PickUpCustomers":[{"CustomerID":"C000000003","CustomerName":"磯法資訊","Total":19500.00},{"CustomerID":"C000000002","CustomerName":"新達科技","Total":13100.00},{"CustomerID":"C000000001","CustomerName":"大島屋企業","Total":9400.00}]}
                    json2 = new ArrayList<>();
                    try {
                        JSONObject j = new JSONObject(json);
                        for (int i = 0; i < j.getJSONArray("PickUpCustomers").length(); i++) {
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
                                if (index != 0) {
                                    ProductInfo cName = (ProductInfo) list.getItem(index);
                                    order = cName.cCustomerName;
                                    Log.e("客戶名稱", order);
                                }

                                Log.e("客戶清單點擊 索引值", String.valueOf(index));
                                Log.e("客戶清單點擊 內容", name);
                                //點擊後所要執行的方法 並把所回傳的json和索引值帶入
                                if (isConnected()) {
                                    postjson2(String.valueOf(json2), index);
                                    setDialog2();
                                }else {
                                    Toast.makeText(ShipperActivity.this,"請確認網路是否有連線",Toast.LENGTH_SHORT).show();
                                }

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
                                    String json = "{\"Token\":\"\" ,\"Action\":\"pickups\",\"UserID\":\"" + cUserID + "\",\"CustomerID\":\"" + door1 + "\"}";
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
                                            myDialog2.dismiss();
                                            //非主執行緒顯示UI(Toast)
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ShipperActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            //取得POST上去後所得到的JSON檔
                                            //{"result":"1","CustomerPickups":[{"PickupNo":"S20160000004","Total":300.00},{"PickupNo":"S20160000014","Total":2000.00}]}
                                            String json3 = response.body().string();
                                            Log.e("取客戶有的撿貨單回傳", json3);
                                            myDialog2.dismiss();
                                            //取出CustomerPickups
                                            //[{"PickupNo":"S20160000004","Total":300},{"PickupNo":"S20160000014","Total":2000}]
                                            try {
                                                JSONObject j = new JSONObject(json3);
                                                json4 = j.getString("CustomerPickups");
                                                Log.e("取CustomerPickups", json4);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            parseJson2(json4);
                                        }
                                    });

                            }

                            //POST成功後把回傳的值(陣列)取出來 用listView顯示 把JSON2帶進來
                            private void parseJson2(String json4) {

                                //ListView 設定
                                final ListView listView = (ListView) findViewById(R.id.listView);

                                try {
                                    upList = new ArrayList();
                                    myList = new ArrayList<Map<String, String>>();
                                    final JSONArray array = new JSONArray(json4);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        map = new LinkedHashMap<String, String>();
                                        map.put("PickupNo", obj.optString("PickupNo"));
                                        map.put("Total", obj.optString("Total"));
                                        map.put("check","0");
                                        myList.add(map);
                                        Log.e("單號和總數量", String.valueOf(myList));

                                    }

                                    adapter = new SpecialAdapter(
                                            ShipperActivity.this,
                                            myList,
                                            R.layout.lview8,
                                            new String[]{"PickupNo", "Total", "checkbox"},
                                            new int[]{R.id.textView31, R.id.textView30, R.id.checkBox3});
                                    setCheckBox();
                                    adapter.notifyDataSetChanged();
                                    //非主執行緒顯示UI
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            //顯示出listView
                                            listView.setVisibility(View.VISIBLE);
                                            CheckBox checkbox = (CheckBox)findViewById(R.id.checkBox6);
                                            checkbox.setVisibility(View.VISIBLE);
                                            //設定 ListView 的接收器, 做為選項的來源
                                            listView.setAdapter(adapter);
                                            mCheckSet.clear();
                                            upList.clear();
                                            adapter.notifyDataSetChanged();
                                            //假如選到請選擇 list將不會出現
                                            if (index == 0) {
                                                listView.setVisibility(View.GONE);
                                                checkbox.setVisibility(View.GONE);

                                            }else {
                                                checkbox.setChecked(false);
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
                                adapter.notifyDataSetChanged();
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }


    }

    public void enter(View v) {

        if(isConnected()){
            ArrayList checked;
            checked = new ArrayList();

            // 把點擊的item放入mcheckset裡面 在一個一個取出放入upl
            Iterator it;
            it = mCheckSet.iterator();
            while (it.hasNext()){
                upList.add(it.next());
            }

            //upList.add(mCheckSet);
            //Log.e("upList.size()", String.valueOf(upList.size()));
            if (upList.size() > 0) {

                for (int i = 0; i < upList.size(); i++) {
                    Log.e("myList.get(i)", String.valueOf(upList.get(i)));
                    int icheck;
                    icheck = Integer.valueOf(String.valueOf(upList.get(i)));
                    checked.add(myList.get(icheck).get("PickupNo"));
                }
                Log.e("checked", String.valueOf(checked));
                //把逗號的空白處取代
                checked2 = String.valueOf(checked).replaceAll(", ", ",");
                int i = checked2.length();
                //再取字串範圍 (0和最後是[])
                //回傳指定範圍(1.i-1)第二個和倒數第二個
                checked3 = checked2.substring(1, i - 1);
                Log.e("CHECK3", checked3);
                Intent intent = new Intent(ShipperActivity.this, ShipperOrderActivity.class);
                Bundle bag = new Bundle();
                bag.putString("checked", checked3);
                bag.putString("order", String.valueOf(order));
                bag.putString("cUserID", cUserID);
                bag.putString("cUserName", cUserName);
                intent.putExtras(bag);
                startActivity(intent);
                ShipperActivity.this.finish();

            }
            //沒有點擊
            else {
                Toast.makeText(ShipperActivity.this, "請選擇出貨單", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this,"請確認網路是否正常",Toast.LENGTH_SHORT).show();
        }



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

    private void setDialog() {
        myDialog = new ProgressDialog(ShipperActivity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }

    private void setDialog2() {
        myDialog2 = new ProgressDialog(ShipperActivity.this);
        myDialog2.setTitle("載入中");
        myDialog2.setMessage("載入資訊中，請稍後！");
        myDialog2.setCancelable(false);
        myDialog2.show();
    }

    private void hideSystemNavigationBar() {


        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
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

    private void setCheckBox() {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox6);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    for (int i = 0; i < myList.size(); i++) {
                        mCheckSet.add(i);
                        adapter.notifyDataSetChanged();
                        Log.e("mCheckSet3", String.valueOf(mCheckSet));

                    }


                } else {
                    for (int i = 0; i < myList.size(); i++) {
                        mCheckSet.clear();
                        adapter.notifyDataSetChanged();
                        Log.e("mCheckSet4", String.valueOf(mCheckSet));
                    }
                }
                checkFull();
            }


        });



    }

    public class SpecialAdapter extends SimpleAdapter {

        public SpecialAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            //获取相应的view中的checkbox对象
            convertView = null;
            if (convertView == null) {
                convertView = View.inflate(ShipperActivity.this, R.layout.lview8, null);
                final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox3);

                Log.e("mCheckSet2", String.valueOf(mCheckSet));
                checkBox.setChecked(mCheckSet.contains(position));
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (((CheckBox) v).isChecked()) {
                            mCheckSet.add(position);
                            Log.e("mCheckSet5", String.valueOf(mCheckSet));
                            //upList.add(position);
                            checkFull();
                            adapter.notifyDataSetChanged();
                        }else {

                            mCheckSet.remove(position);
                            //upList.remove(position);
                            checkFull();
                            adapter.notifyDataSetChanged();
                        }
                        Log.e("UPLIST", String.valueOf(upList));
                        Log.e("mCheckSet", String.valueOf(mCheckSet));
                    }
                });

            }

            return super.getView(position, convertView, parent);
        }

    }
    private void checkFull(){
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox6);
        if(mCheckSet.size() == myList.size()){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
    }
    //判斷網路有無訊號
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void onUpdata (View v ){
        Intent intent = new Intent(this,ShipperActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName",cUserName);
        bag.putString("cUserID",cUserID);
        intent.putExtras(bag);
        startActivity(intent);
        ShipperActivity.this.finish();
    }
}