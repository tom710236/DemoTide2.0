package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    String url = Application.TideUrl + "Pickup.aspx";

    public class ProductInfo {
        private String cCustomerID;
        private String cCustomerName;
        private String ctotal;
        private String cMany;


        ProductInfo(String CusStomerID, String CustomerName, String Total,String Many) {
            this.cCustomerID = CusStomerID;
            this.cCustomerName = CustomerName;
            this.ctotal = Total;
            this.cMany = Many;
        }

        public String toString() {
            return this.cCustomerName + "(" + this.ctotal + ")"+"-"+this.cMany+"筆";
        }
    }

    public class CustomerInfo {
        private String cPickupNo;
        private String cTotal;
        private String cGift;

        CustomerInfo(String PickupNo, String Total, String Gift) {
            this.cPickupNo = PickupNo;
            this.cTotal = Total;
            this.cGift = Gift;
        }
        public String toString() {
            return this.cPickupNo + "  " + this.cTotal +"("+this.cGift +")";
        }
    }

    ArrayList jsonArray, json2Array;
    ArrayList trans, trans2;

    String IDSpinner;
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> myList;
    Map<String, String> map;
    ArrayList upList;
    private HashSet<Integer> mCheckSet = new HashSet<Integer>();
    ProgressDialog myDialog, myDialog2;
    boolean checkGift = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper);

        toolBar();
        Post post = new Post();
        post.run();
        setDialog();
        setCheckBox();

        hideSystemNavigationBar();
    }

    private void toolBar() {
        Log.e("NAME2", Application.UserName);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(Application.UserName + "您好");

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
                Intent intent = new Intent(ShipperActivity.this, AllListActivity.class);
                startActivity(intent);
                ShipperActivity.this.finish();
            }
        });
    }

    //Post 取得客戶資料
    class Post extends Thread {
        @Override
        public void run() {
            super.run();
            PostCustomerInfo();
        }

        private void PostCustomerInfo() {

            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"customer\",\"UserID\":\"" + Application.UserID + "\"}";
            Log.e("客戶API上傳JSON", json);
            final RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            okhttp3.Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String PickUpCustomers = response.body().string();
                    Log.e("客戶API回傳", PickUpCustomers);
                    parseJson(PickUpCustomers);
                    myDialog.dismiss();
                }
            });

        }

        //解析json
        private void parseJson(String json) {
            try {
                jsonArray = new ArrayList<>();
                JSONObject j = new JSONObject(json);
                for (int i = 0; i < j.getJSONArray("PickUpCustomers").length(); i++) {
                    //取customerID
                    //jsonArray.add(j.getJSONArray("PickUpCustomers").getJSONObject(i).get("CustomerID"));
                    jsonArray.add(j.getJSONArray("PickUpCustomers").get(i));
                    Log.e("客戶回傳分析", String.valueOf(jsonArray.get(i)));
                }

                trans = new ArrayList<>();
                JSONArray array = new JSONArray(jsonArray);
                trans.add("請選擇");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    trans.add(new ProductInfo(obj.optString("CustomerID"), obj.optString("CustomerName"), obj.optString("Total"),obj.optString("OrderCount")));
                }
                Log.e("客戶分析2", String.valueOf(trans));
                setSpinner(trans);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSpinner(final ArrayList trans) {
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter list = new ArrayAdapter<>(
                ShipperActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                trans
        );
        //顯示Spinner 非主執行緒的UI 需用runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setAdapter(list);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    ProductInfo cID = (ProductInfo) list.getItem(position);
                    IDSpinner = cID.cCustomerID;
                    Log.e("spinner 客戶ID", IDSpinner);

                    if (isConnected()) {
                        PostCustomerID postID = new PostCustomerID();
                        postID.start();
                        setDialog2();
                    }

                }else {
                    ListView listView = (ListView) findViewById(R.id.listView);
                    CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox6);
                    listView.setVisibility(View.GONE);
                    checkBox.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    class PostCustomerID extends Thread {
        @Override
        public void run() {
            super.run();
            PostCustomerIDInfo();

        }

        private void PostCustomerIDInfo() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            //取客戶有的撿貨單
            String json = "{\"Token\":\"\" ,\"Action\":\"pickups\",\"UserID\":\"" + Application.UserID + "\",\"CustomerID\":\"" + IDSpinner + "\"}";
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

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String customerPickupsJson = response.body().string();
                    Log.e("取客戶有的撿貨單回傳", customerPickupsJson);
                    parse2Json(customerPickupsJson);

                }
            });
        }

        private void parse2Json(String json) {
            json2Array = new ArrayList<>();

            try {
                JSONObject j2 = new JSONObject(json);
                json2Array = new ArrayList<>();
                for (int i = 0; i < j2.getJSONArray("CustomerPickups").length(); i++) {
                    json2Array.add(j2.getJSONArray("CustomerPickups").get(i));
                    Log.e("客戶檢貨單回傳分析", String.valueOf(json2Array.get(i)));
                }
                trans2 = new ArrayList<>();
                myList = new ArrayList<Map<String, String>>();

                JSONArray array = new JSONArray(json2Array);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    trans2.add(new CustomerInfo(obj.optString("PickupNo"), obj.optString("Total"),"贈品"));
                    map = new LinkedHashMap<String, String>();
                    map.put("PickupNo", obj.optString("PickupNo"));
                    map.put("Total", obj.optString("Total"));
                    if (obj.optString("IsGift") == "1" ){
                        map.put("Gift","贈品");
                    }

                    map.put("check","0");
                    myList.add(map);
                    Log.e("客戶OBJ", String.valueOf(myList));
                }
                setListView(myList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void setListView(ArrayList myList) {
        final ListView listView = (ListView) findViewById(R.id.listView);
        final CheckBox checkbox = (CheckBox)findViewById(R.id.checkBox6);
        adapter = new SpecialAdapter(
                ShipperActivity.this,
                myList,
                R.layout.lview8,
                new String[]{"PickupNo", "Total","Gift", "checkbox"},
                new int[]{R.id.textView31, R.id.textView30,R.id.textView50, R.id.checkBox3});

        adapter.notifyDataSetChanged();


        //顯示Spinner 非主執行緒的UI 需用runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setVisibility(View.VISIBLE);
                checkbox.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
                myDialog2.dismiss();
                hideSystemNavigationBar();
            }
        });



    }
    public class SpecialAdapter extends SimpleAdapter {

        public SpecialAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            convertView = null;
            if (convertView == null) {
                convertView = View.inflate(ShipperActivity.this, R.layout.lview8, null);
                final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox3);
                checkBox.setChecked(mCheckSet.contains(position));
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (((CheckBox) v).isChecked()) {
                            mCheckSet.add(position);
                            Log.e("點選", String.valueOf(myList.get(position)));
                            adapter.notifyDataSetChanged();
                        }else {
                            mCheckSet.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        checkFull();
                    }
                });

            }
            return super.getView(position, convertView, parent);
        }
    }
    public void enter(View v) {
        Log.e("checkGift", String.valueOf(checkGift()));
        if (checkGift() == true){
            //取出HashSet的值
            Iterator it = mCheckSet.iterator();
            upList = new ArrayList();
            for (int i = 0 ; i <mCheckSet.size() ; i ++){
                int index = Integer.parseInt(String.valueOf(it.next())) ;
                upList.add(String.valueOf(myList.get(index).get("PickupNo")));
            }
            //調整上傳的文字格式
            String check = String.valueOf(upList);
            String check2 = check.replaceAll(", ", ",");
            String check3 = check2.substring(1, check2.length()-1);
            Application.check3 = check3;
            Log.e("檢貨", String.valueOf(check3));

            if (mCheckSet.size() != 0 ) {
                Intent intent = new Intent(this, ShipperOrderActivity.class);
                intent.putExtra("checkGift",checkGift);
                startActivity(intent);
                adapter.notifyDataSetChanged();
            }
        }else {
            setGiftDialog();
        }

    }

    public void onUpdata (View v){
        Intent intent = new Intent(this,ShipperActivity.class);
        startActivity(intent);
        ShipperActivity.this.finish();
    }


    //判斷網路有無訊號
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    //設定 Dialog
    private void setDialog() {
        myDialog = new ProgressDialog(ShipperActivity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }
    //設定 Dialog
    private void setDialog2() {
        myDialog2 = new ProgressDialog(ShipperActivity.this);
        myDialog2.setTitle("載入中");
        myDialog2.setMessage("載入資訊中，請稍後！");
        myDialog2.setCancelable(false);
        myDialog2.show();
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
    //假如checkbox都勾 checkbox6也要勾
    private void checkFull(){
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox6);
        if(mCheckSet.size() == myList.size()){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
    }

    //設定返回鍵 和 拍照鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_CAMERA) { // 攔截返回鍵
            return true;
        }
        //return super.onKeyDown(keyCode, event);
        return false;
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
    //設定回到桌面在返回後 三鍵還是隱藏
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

    private boolean checkGift(){
        int i2 = 0;
        int i3 = 0;
        //取出HashSet的值
        Iterator it = mCheckSet.iterator();
        upList = new ArrayList();
        for (int i = 0 ; i <mCheckSet.size() ; i ++){
            int index = Integer.parseInt(String.valueOf(it.next())) ;
            if(myList.get(index).get("Gift")!=null){
                i2++;
            }
            if(myList.get(index).get("PickupNo") != null){
                i3++;
            }
            Log.e("贈品",(String.valueOf(myList.get(index).get("Gift"))));
            Log.e("贈品", String.valueOf(i2));
        }
        if (i2<1){
            return true;
        }else if (i2 == 1){
            if(i3<2){
                checkGift = true;
                return true;
            }else {
                return false;
            }

        }else {
            return false;
        }
    }

    private void setGiftDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("檢貨單中含有贈品")
                .setNegativeButton("確定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        }).show();



    }
}