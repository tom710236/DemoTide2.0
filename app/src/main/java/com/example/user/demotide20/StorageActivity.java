package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StorageActivity extends AppCompatActivity {
    String cUserName, cUserID,mLackNO,mLackName,lackNoAdd,lackNameAdd,scr;
    String url="http://demo.shinda.com.tw/ModernWebApi/LackAPI.aspx";
    ArrayList<Map<String, String>> myList;
    int iMax = 0,indexSpinner;
    Map<String, String> map;
    ListView listView;
    SimpleAdapter adapter;
    ArrayList upList,checkList;
    ProgressDialog myDialog;
    CheckBox checkBox;
    int remInt;
    private HashSet<Integer> mCheckSet = new HashSet<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        toolBar();
        getPreviousPage();
        Post post = new Post();
        post.start();
        setDialog();

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
        Intent intent = new Intent(StorageActivity.this, AllListActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID", cUserID);
        bag.putInt("indexSpinner",indexSpinner);
        intent.putExtras(bag);
        startActivity(intent);
        StorageActivity.this.finish();
    }

    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        indexSpinner = bag.getInt("indexSpinner",0);
        Log.e("cUserID", cUserID);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }

    class Post extends Thread {
        @Override
        public void run() {
            PostLackInfo();
        }

        private void PostLackInfo() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"list\",\"Search\":\"\" }";
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
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StorageActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    myDialog.dismiss();
                    String json = response.body().string();
                    Log.e("取得list清單的網址", response.toString());
                    Log.e("取得的list清單", json);
                    String json2 = null;
                    try {
                        JSONObject j = new JSONObject(json);
                        json2 = j.getString("LackList");
                        Log.e("取出LackList", json2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    parseJson(json2);
                }
            });
        }

        private void parseJson(String json2) {
            myList = new ArrayList<Map<String, String>>();
            try {
                final JSONArray array = new JSONArray(json2);
                for (iMax = 0; iMax < array.length(); iMax++) {
                    JSONObject obj = array.getJSONObject(iMax);
                    map = new HashMap<String, String>();
                    map.put("LackName",obj.optString("LackName"));
                    map.put("LackNo",obj.optString("LackNo"));
                    map.put("-","-");
                    map.put("checkbox","");
                    myList.add(map);
                }
                Log.e("mylist", String.valueOf(myList));
                setLackListView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    private void setLackListView(){


        listView = (ListView) findViewById(R.id.list);
        upList = new ArrayList();
        checkList = new ArrayList();
        adapter = new SimpleAdapter(
                StorageActivity.this,
                myList,
                R.layout.lview5,
                new String[]{"LackNo", "-", "LackName", "checkbox"},
                new int[]{R.id.textView28, R.id.textView27, R.id.textView26, R.id.checkBox}){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                //获取相应的view中的checkbox对象
                if(convertView == null)
                    convertView = View.inflate(StorageActivity.this, R.layout.lview5, null);
                checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
                checkBox.setChecked(mCheckSet.contains(position));
                checkBox.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(((CheckBox)v).isChecked()){
                            upList.add(myList.get(position).get("LackNo"));
                            mCheckSet.add(position);
                        }else{
                            upList.remove(myList.get(position).get("LackNo"));
                            mCheckSet.remove(position);


                        }
                        Log.e("UPLIST", String.valueOf(upList));
                        Log.e("mCheckSet", String.valueOf(mCheckSet));

                    }
                });

                return super.getView(position, convertView, parent);
            }

        };

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                hideSystemNavigationBar();
            }
        });
        //長按
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mLackNO = myList.get(position).get("LackNo");
                mLackName = myList.get(position).get("LackName");

                Log.e("LackNO",mLackNO);
                Log.e("LackName",mLackName);
                toOrder();
                return false;
            }

            private void toOrder() {
                Intent intent = new Intent(StorageActivity.this, StorageOrderActivity.class);
                Bundle bag = new Bundle();
                bag.putString("mLackNO",mLackNO);
                bag.putString("mLackName",mLackName);
                bag.putString("cUserName",cUserName);
                bag.putString("cUserID",cUserID);
                intent.putExtras(bag);
                startActivity(intent);
            }
        });
    }
    //新增儲位
    public void onAdd (View v){

        //對話框
        final View item = LayoutInflater.from(StorageActivity.this).inflate(R.layout.item2, null);
        new AlertDialog.Builder(StorageActivity.this)
                .setTitle("")
                .setView(item)
                .setNegativeButton("取消", null)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText lackNo =(EditText)item.findViewById(R.id.editText4);
                        EditText lackName = (EditText)item.findViewById(R.id.editText6);
                        lackNoAdd = lackNo.getText().toString();
                        lackNameAdd = lackName.getText().toString();
                        if(lackNoAdd.length()!=0||lackNameAdd.length()!=0){
                            Pass pass = new Pass();
                            pass.start();
                            setDialog();


                        }else {
                            Toast.makeText(StorageActivity.this, "請輸入儲位名稱或編號", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).show();

    }
    //onAdd的方法 POST 新增Lack JSON的方法
    class Pass extends Thread {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\": \"xxxxxxxxxxxxxxxxxxxxxxxx\", \"Action\": \"add\", \"UserID\": \"test\", \"LackInfo\": {\"LackNo\": \""+lackNoAdd+"\", \"LackName\": \""+lackNameAdd+"\"}}";
            Log.e("JSON",json);
            RequestBody body = RequestBody.create(JSON,json);
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
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StorageActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    myDialog.dismiss();
                    String json = response.body().string();
                    Log.e("OkHttp", response.toString());
                    Log.e("OkHttp2", json);
                    Post post = new Post();
                    post.start();
                    //setDialog();
                }
            });
        }
    }
    //刪除
    //刪除按鈕
    public void onDel (View v){
        PassDel passDel = new PassDel();
        passDel.start();
        setDialog();
        mCheckSet.clear();
    }
    class PassDel extends Thread {
        @Override
        public void run() {
            postPassDel();
        }
        private void postPassDel(){
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String checked3 = String.valueOf(upList).replace("[", "");
            String upList2 = checked3.replace("]", "");
            String upList3 = upList2.replace(", ", ",");
            String json = "{\"Token\":\"\" ,\"Action\":\"delete\",\"LackNo\":\"" + upList3 + "\"}";
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
                @Override
                public void onFailure(Call call, IOException e) {
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StorageActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    myDialog.dismiss();
                    String json = response.body().string();
                    Log.e("OkHttp5", response.toString());
                    Log.e("OkHttp6", json);
                    Post post = new Post();
                    post.start();


                }
            });
        }
    }
    public void onScr (View v){
        EditText editText = (EditText)findViewById(R.id.editText5);
        scr = editText.getText().toString();
        PostonScr postonScr = new PostonScr();
        postonScr.start();
        setDialog();
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
    }
    class PostonScr extends Thread {
        @Override
        public void run() {
            PostLackInfo();
        }

        private void PostLackInfo() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"list\",\"Search\":\""+scr+"\" }";
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
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StorageActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    myDialog.dismiss();
                    String json = response.body().string();
                    Log.e("取得list清單的網址", response.toString());
                    Log.e("取得的list清單", json);
                    String json2 = null;
                    try {
                        JSONObject j = new JSONObject(json);
                        json2 = j.getString("LackList");
                        Log.e("取出LackList", json2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    parseJson(json2);
                }
            });
        }

        private void parseJson(String json2) {
            myList = new ArrayList<Map<String, String>>();
            try {
                final JSONArray array = new JSONArray(json2);
                for (iMax = 0; iMax < array.length(); iMax++) {
                    JSONObject obj = array.getJSONObject(iMax);
                    map = new HashMap<String, String>();
                    map.put("LackName",obj.optString("LackName"));
                    map.put("LackNo",obj.optString("LackNo"));
                    map.put("-","-");
                    map.put("checkbox","");
                    myList.add(map);
                }
                Log.e("mylist", String.valueOf(myList));
                setLackListView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    public void onClear (View v){
        PassClear passClear = new PassClear();
        passClear.start();
        setDialog();

    }
    class PassClear extends Thread {
        @Override
        public void run() {
            postPassClear();
        }
        private void postPassClear(){
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String checked3 = String.valueOf(upList).replace("[", "");
            String upList2 = checked3.replace("]", "");
            String upList3 = upList2.replace(", ", ",");

            String json = "{\"Token\":\"\" ,\"Action\":\"clearprod\",\"LackNo\":\"" + upList3 + "\"}";
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
                @Override
                public void onFailure(Call call, IOException e) {
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StorageActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    myDialog.dismiss();
                    String json = response.body().string();
                    Log.e("OkHttp5", response.toString());
                    Log.e("OkHttp6", json);
                    changeEnd(json);

                }
            });
        }
    }
    //POST成功後取得1跳回前一頁
    private void changeEnd(String json) {
        int result = 0;
        try {
            result = new JSONObject(json).getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 1 && upList.size()!=0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(StorageActivity.this, "商品已清空", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(StorageActivity.this, "商品未清空或未選擇儲位", Toast.LENGTH_SHORT).show();
                }
            });

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
    private void setDialog(){
        hideSystemNavigationBar();
        myDialog = new ProgressDialog(this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();

    }
}