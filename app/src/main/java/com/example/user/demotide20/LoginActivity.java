package com.example.user.demotide20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    String cStatus, userName, passWord, cUserName,cUserID;
    //帳號登入的API
    String url = "http://demo.shinda.com.tw/ModernWebApi/WebApiLogin.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //帳號若輸入正確 記住登入帳號
        SharedPreferences setting =
                getSharedPreferences("Login", MODE_PRIVATE);
        EditText uId = (EditText) findViewById(R.id.userName);
        uId.setText(setting.getString("userName", ""));

    }
    // 登入鍵 - 執行執行緒
    public void login (View v){
        Post post = new Post();
        post.start();
    }
    // 執行緒 - 執行PostUserInfo()方法
    class Post extends Thread{
        @Override
        public void run() {
            PostUserInfo();
        }
    }
    //把輸入的帳號密碼轉成JSON 用OkHttp Post登入API
    private void PostUserInfo() {
        //輸入帳號密碼
        final EditText uId = (EditText) findViewById(R.id.userName);
        EditText uPw = (EditText) findViewById(R.id.passWord);
        userName = uId.getText().toString();
        passWord = uPw.getText().toString();

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"cAccount\":\""+userName+"\",\"cPassword\":\""+passWord+"\"}";
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
                        Toast.makeText(LoginActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                Log.e("POST後的回傳值", json);
                //所要執行的方法 - 解析JSON
                parseJson(json);
            }
        });
    }

    private void parseJson(String json) {

        try {
            //從回傳資料json 抓取cStatus項目裡的內容
            cStatus = new JSONObject(json).getString("result");
            //從回傳資料json 抓取cUserName項目內的內容
            Log.e("result",cStatus);


            if (cStatus.equals("1")) {
                //確定登入後再抓回傳的值 不然會一直try
                cUserName = new JSONObject(json).getString("UserName");
                Log.e("UserName", cUserName);
                cUserID = new JSONObject(json).getString("UserID");
                Log.e("UserID",cUserID);
                //另一頁 用Bundle把所需資料帶到另一頁
                Intent intent = new Intent(LoginActivity.this, AllListActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID",cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                LoginActivity.this.finish();
                //記住帳號
                SharedPreferences setting =
                        getSharedPreferences("Login", MODE_PRIVATE);
                setting.edit()
                        .putString("userName", userName)
                        .commit();

            } else if (cStatus.equals("0")) {
                Log.e("NO","NO");
                //非主執行緒顯示UI(Toast)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "登入失敗 請重新輸入", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}