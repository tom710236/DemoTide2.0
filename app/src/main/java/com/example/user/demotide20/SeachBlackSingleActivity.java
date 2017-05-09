package com.example.user.demotide20;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SeachBlackSingleActivity extends AppCompatActivity {
    String url = "http://demo.shinda.com.tw/ModernWebApi/Blank.aspx";
    ArrayList mWarehouse,mInputType;
    String cUserName,cUserID,Sname,scrData,mBlackNo;
    Object Warehouse,BlankNo,WHTypeName;
    Object InputType;
    int index,indexSpinner;
    SpecialAdapter adapter;
    ArrayList<Map<String, String>> myList;
    Map<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach_black_single);
        toolBar();
        getPreviousPage();
        PostGetType postGetType = new PostGetType();
        postGetType.start();
        setEditText();
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
                Intent intent = new Intent(SeachBlackSingleActivity.this, AllListActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID",cUserID);
                bag.putInt("indexSpinner",indexSpinner);
                intent.putExtras(bag);
                startActivity(intent);
                SeachBlackSingleActivity.this.finish();
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
        indexSpinner = bag.getInt("indexSpinner",0);
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
                            Toast.makeText(SeachBlackSingleActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
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
                for(int i2 = 0; i2<j2.getJSONArray("InputType").length();i2++){
                    InputType = j2.getJSONArray("InputType").getJSONObject(i2).get("Name");
                    mInputType.add(InputType);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("InputType", String.valueOf(mInputType));
            //把資料放進Spinner
            mSpinner(mInputType,mWarehouse);
        }
        //放進Spinner的方法
        private void mSpinner(ArrayList mInputType, ArrayList mWarehouse) {
            final Spinner SInputType = (Spinner) findViewById(R.id.spinner5);
            final ArrayAdapter<String> list = new ArrayAdapter<>(
                    SeachBlackSingleActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    mInputType);
            Log.e("InputType", String.valueOf(SeachBlackSingleActivity.this.mInputType));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SInputType.setAdapter(list);
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
                    Log.e("Sname", Sname);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // sometimes you need nothing here
                }
            });
            
        }

    }
    class PostSearch extends Thread{
        @Override
        public void run() {

            PostGetSearchInfo();
        }

        private void PostGetSearchInfo() {

            final OkHttpClient client = new OkHttpClient();
            //要上傳的內容(JSON)--帳號登入
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"list\",\"SearchType\":"+index+",\"SearchDate\":\""+scrData+"\"}";
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
                            Toast.makeText(SeachBlackSingleActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
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

            private void paraJson(String json) {

                myList = new ArrayList<>();

                try {
                        JSONObject j2 = new JSONObject(json);
                        for(int i=0 ; i< j2.getJSONArray("BlankList").length();i++){
                            map = new LinkedHashMap<String, String>();
                            BlankNo = j2.getJSONArray("BlankList").getJSONObject(i).get("BlankNo");
                            WHTypeName = j2.getJSONArray("BlankList").getJSONObject(i).get("WHTypeName");
                            map.put("BlankNo", String.valueOf(BlankNo));
                            map.put("-", "-");
                            map.put("WHTypeName", String.valueOf(WHTypeName));
                            myList.add(map);
                            Log.e("myList", String.valueOf(myList));
                    }

                    setArraylist();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
    }
    public void onScr (View v){
        EditText editText = (EditText)findViewById(R.id.editText5);
        scrData = editText.getText().toString();
        PostSearch postSearch = new PostSearch();
        postSearch.start();

    }
    private void setArraylist(){

        final ListView listView = (ListView) findViewById(R.id.list);
        adapter = new SeachBlackSingleActivity.SpecialAdapter(
                SeachBlackSingleActivity.this,
                myList,
                R.layout.lview7,
                new String[]{"BlankNo", "-", "WHTypeName"},
                new int[]{R.id.textView46, R.id.textView45, R.id.textView43});
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);

            }
        });
        //長按
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mBlackNo = myList.get(position).get("BlankNo");

                Log.e("mBlackNo",mBlackNo);
                toOrder();
                return false;
            }

            private void toOrder() {
                Intent intent = new Intent(SeachBlackSingleActivity.this, SearchBlackSingleListActivity.class);
                Bundle bag = new Bundle();
                bag.putString("mBlackNo",mBlackNo);
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID",cUserID);
                intent.putExtras(bag);
                startActivity(intent);
            }
        });
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
    private void setEditText(){
        String today;
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy-MM-dd";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today = df.format(mCal.getTime());
        EditText editText = (EditText)findViewById(R.id.editText5);
        editText.setText(today);
    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(SeachBlackSingleActivity.this)
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
