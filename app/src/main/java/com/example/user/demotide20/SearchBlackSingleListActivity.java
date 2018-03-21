package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchBlackSingleListActivity extends AppCompatActivity {
    String cUserName, cUserID, scrData, mBlackNo, cProductName;
    //String url = "http://demo.shinda.com.tw/ModernWebApi/Blank.aspx";
    //String url = "192.168.0.2:8011/Blank.aspx";
    String url = Application.TideUrl+"Blank.aspx";
    LinkedHashMap<String, String> map;
    ArrayList<LinkedHashMap<String, String>> myList;
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    SpecialAdapter adapter;
    Object BlankNo, BlankTypeName, WHTypeName, UserName, FinishDate,BlackType,WHType;
    int indexSpinner;
    ProgressDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_black_single_list);
        getPreviousPage();
        toolBar();
        PostGetDetial postGetDetial = new PostGetDetial();
        postGetDetial.start();
        setDialog();

    }

    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        //cUserName = bag.getString("cUserName", null);
        //cUserID = bag.getString("cUserID", null);
        cUserName = Application.UserName;
        cUserID = Application.UserID;
        mBlackNo = bag.getString("mBlackNo", null);
        indexSpinner = bag.getInt("indexSpinner",0);
        Log.e("mBlackNo", mBlackNo);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }

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
        Intent intent = new Intent(SearchBlackSingleListActivity.this, SeachBlackSingleActivity.class);
        Bundle bag = new Bundle();
        bag.putString("cUserName", cUserName);
        bag.putString("cUserID", cUserID);
        bag.putInt("indexSpinner",indexSpinner);
        intent.putExtras(bag);
        startActivity(intent);
        SearchBlackSingleListActivity.this.finish();
    }

    class PostGetDetial extends Thread {
        @Override
        public void run() {
            PostGetDetialInfo();
        }

        //Post getType
        private void PostGetDetialInfo() {

            final OkHttpClient client = new OkHttpClient();
            //要上傳的內容(JSON)--帳號登入
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"detail\",\"BlankNo\":\"" + mBlackNo + "\"}";
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
                            Toast.makeText(SearchBlackSingleListActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
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
                    //解析 JSON
                    paraJson(json);

                }
            });
        }

        //解析JSON的方法 並把取出的資料放進arrayList
        private void paraJson(String json) {

            myList = new ArrayList<>();
            try {
                JSONObject j = new JSONObject(json);
                BlankNo = j.getJSONArray("BlankList").getJSONObject(0).get("BlankNo");
                BlankTypeName = j.getJSONArray("BlankList").getJSONObject(0).get("BlankTypeName");
                WHTypeName = j.getJSONArray("BlankList").getJSONObject(0).get("WHTypeName");
                UserName = j.getJSONArray("BlankList").getJSONObject(0).get("UserName");
                FinishDate = j.getJSONArray("BlankList").getJSONObject(0).get("FinishDate");
                BlackType = j.getJSONArray("BlankList").getJSONObject(0).get("BlankType");
                WHType = j.getJSONArray("BlankList").getJSONObject(0).get("WHType");
                Log.e("BlankTypeName", String.valueOf(BlankTypeName));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView t1 = (TextView)findViewById(R.id.textView41);
                        TextView t2 = (TextView)findViewById(R.id.textView46);
                        TextView t3 = (TextView)findViewById(R.id.textView43);
                        TextView t4 = (TextView)findViewById(R.id.textView44);
                        TextView t5 = (TextView)findViewById(R.id.textView45);
                        t1.setText(String.valueOf(BlankNo));
                        t2.setText(String.valueOf(BlankTypeName));
                        t3.setText(String.valueOf(WHTypeName));
                        t4.setText(String.valueOf(UserName));
                        t5.setText(String.valueOf(FinishDate));
                    }
                });


                for (int i = 0; i < j.getJSONArray("BlankList").getJSONObject(0).getJSONArray("BlankProduct").length(); i++) {
                    Object BlankProduct = j.getJSONArray("BlankList").getJSONObject(0).getJSONArray("BlankProduct").get(i);
                    JSONObject j2 = new JSONObject(String.valueOf(BlankProduct));
                    Log.e("BlankList", j2.optString("ProductNo"));
                    setThingSQL();
                    Cursor c = db.query("tblTable",                            // 資料表名字
                            null,                                              // 要取出的欄位資料
                            "cProductID=?",                                    // 查詢條件式(WHERE)
                            new String[]{j2.optString("ProductNo")},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                            null,                                              // Group By字串語法
                            null,                                              // Having字串法
                            null);                                             // Order By字串語法(排序)

                    while (c.moveToNext()) {
                        cProductName = c.getString(c.getColumnIndex("cProductName"));
                        Log.e("cProductName", cProductName);
                    }
                    map = new LinkedHashMap<String, String>();
                    map.put("cProductName", cProductName);
                    map.put("ProductNo", j2.optString("ProductNo"));
                    map.put("Qty", j2.optString("Qty"));
                    map.put("NOWQty","");
                    myList.add(map);
                    Log.e("myList", String.valueOf(myList));

                }
                setLackListView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    //商品清單SQL
    private void setThingSQL() {
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        db = helper.getWritableDatabase();
    }

    private void setLackListView() {
        final ListView listView = (ListView) findViewById(R.id.list);
        adapter = new SearchBlackSingleListActivity.SpecialAdapter(
                SearchBlackSingleListActivity.this,
                myList,
                R.layout.lview9,
                new String[]{"cProductName", "ProductNo", "Qty","NOWQty"},
                new int[]{R.id.textView21, R.id.textView22, R.id.textView23,R.id.textView24});
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }
    public class SpecialAdapter extends SimpleAdapter {
        private int[] colors = new int[] { 0x30ffffff, 0x30696969 };

        public SpecialAdapter(Context context, ArrayList<LinkedHashMap<String, String>> items, int resource, String[] from, int[] to) {
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
        myDialog = new ProgressDialog(this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }

    public void onChange (View v){
        Log.e("mBlackNo",mBlackNo);
        Log.e("mBlackNo", String.valueOf(myList));
        Intent intent = new Intent(this,BlackSingleActivity.class);
        Application.checkChange = true;
        Application.cBlankNo = mBlackNo;
        Application.cMyList = myList;
        Application.cBlankType = String.valueOf(BlackType);
        Application.cWHType = String.valueOf(WHType);
        startActivity(intent);
    }
}