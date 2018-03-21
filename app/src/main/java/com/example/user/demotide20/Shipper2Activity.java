package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class Shipper2Activity extends AppCompatActivity {
    String url = Application.TideUrl + "Pickup.aspx";
    String mOrder;
    ProgressDialog myDialog;
    SpecialAdapter adapter;
    //SimpleAdapter adapter;
    ArrayList<LinkedHashMap<String, String>> myList;
    LinkedHashMap<String, String> map;
    //兩筆資料以上
    String[] stringArray;
    ArrayList  Btrans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper2);

        Btrans = new ArrayList();
        myList = new ArrayList<LinkedHashMap<String, String>>();
        enterExitText();
        setListView();
        toolBar();
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
                Intent intent = new Intent(Shipper2Activity.this, ShipperCheck.class);
                startActivity(intent);
                Shipper2Activity.this.finish();
            }
        });
    }

    class PostShipper extends Thread{

        public void run(String order) {
            postUserInfo(order);
            super.run();
        }

        private void postUserInfo(final String order) {
            Log.e("登入URL",url);
            final OkHttpClient client = new OkHttpClient();
            //要上傳的內容(JSON)--帳號登入
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"checkorder\",\"PickupNumbers\":\"" + order + "\"}";
            Log.e("POST",json);
            RequestBody body = RequestBody.create(JSON,json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    myDialog.dismiss();
                    //非主執行緒顯示UI(Toast)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Shipper2Activity.this, "查詢失敗 請重新輸入", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("POST後的回傳值", json);
                    myDialog.dismiss();
                    parseJson(json);

                }

            });

        }

        private void parseJson(String json) {
            String result;

            map = new LinkedHashMap<String, String>();
            try {
                result = new JSONObject(json).getString("result");
                Log.e("回傳解析",result);
                if(result.equals("1")){
                    map.put("order",mOrder);
                    myList.add(map);
                    for (int i = 0 ; i<myList.size() ; i++){
                        Log.e("map",myList.get(i).toString());
                    }
                    setListView();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void enter (View v){
        EditText editText = (EditText) findViewById(R.id.editText);
        mOrder = editText.getText().toString();


        Btrans.add(mOrder);
        Log.e("Btrans", String.valueOf(Btrans));
        stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
        for(int i = 0 ; i<stringArray.length ; i++){
            Log.e("string",stringArray[i]);
        }
        Log.e("check2ID(stringArray)",check2ID(stringArray));
        if("".equals(mOrder.trim())){
            Log.e ("空白","空白");
        }else if(check2ID(stringArray).equals("many")){
            Log.e("重複","重複");
        }else {
            setDialog();
            PostShipper post = new PostShipper();
            post.run(mOrder);
            editText.requestFocus();
            //editText.setText("");
        }
    }
    //設定 Dialog
    private void setDialog() {
        myDialog = new ProgressDialog(Shipper2Activity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }

    //虛擬鍵盤按下enter
    private void enterExitText(){
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if("".equals(editText.getText().toString().trim())){
                    Log.e ("空白","空白");
                }else {
                    setDialog();
                    PostShipper post = new PostShipper();
                    post.run(mOrder);
                    editText.requestFocus();
                    editText.setText("");
                }
                return false;
            }
        });
    }

    private void setListView() {
        final ListView listView = (ListView) findViewById(R.id.list);

        Log.e("清單", String.valueOf(myList));
        adapter = new SpecialAdapter(
                Shipper2Activity.this,
                myList,
                R.layout.lview10,
                new String[]{"order"},
                new int[]{R.id.textView53});
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);

            }
        });



    }

    public class SpecialAdapter extends SimpleAdapter {
        //背景顏色
        private int[] colors = new int[]{0x30ffffff, 0x30696969};
        //檢滿後字體顏色
        private int colors2 = Color.BLUE;

        public SpecialAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            int colorPos = position % colors.length;
            view.setBackgroundColor(colors[colorPos]);
            convertView = null;
            //要先初始化顏色 不然往下拉時 item會被吃掉
            if (convertView == null) {
                TextView textView53 = (TextView) view.findViewById(R.id.textView53);
                textView53.setTextColor(Color.BLACK);
            }
            return view;
        }
    }
    private String check2ID(String[] cProductIDeSQL) {
        int checkNum = 0;
        String cProductIDeSQL2 = null;

        for (int i = 0 ; i< cProductIDeSQL.length ; i ++){
            Log.e("cProductIDeSQL", cProductIDeSQL[i]);
            for (int i2 = 0; i2 < myList.size(); i2++) {
                if (cProductIDeSQL[i].equals(myList.get(i2).get("order"))) {
                    checkNum = 1;
                }else checkNum = 0;
            }
        }
        if(checkNum == 0){
            Log.e("checkNum","nothing");
            return "nothing";

        }else if (checkNum == 1){
            return "many";
        }else {
            return "many";
        }
    }
}
