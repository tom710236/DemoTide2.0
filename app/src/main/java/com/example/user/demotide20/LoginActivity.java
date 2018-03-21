package com.example.user.demotide20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
    String url = Application.TideUrl+"WebApiLogin.aspx";
    String mUserName,mPassWord;
    ProgressDialog myDialog;
    String upDate ="V1.24"; //版本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //顯示版本
        TextView textView = (TextView)findViewById(R.id.textView14);
        textView.setText(upDate);
        Application.upDate = upDate;
        //帳號若輸入正確 記住登入帳號
        SharedPreferences setting =
                getSharedPreferences("Login", MODE_PRIVATE);
        EditText uId = (EditText) findViewById(R.id.userName);
        uId.setText(setting.getString("userName", ""));

    }

    public void login (View v){

        EditText userName = (EditText)findViewById(R.id.userName);
        mUserName = userName.getText().toString();

        EditText passWord = (EditText)findViewById(R.id.passWord);
        mPassWord = passWord.getText().toString();
        hideSystemNavigationBar();
        if(isConnected()){
            PostID post = new PostID();
            post.run(mUserName,mPassWord);
            setDialog();
        }else {
            Toast.makeText(this,"請確認網路是否有連線",Toast.LENGTH_SHORT).show();
        }

    }


    class PostID extends Thread{

        public void run(String mUserName, String mPassWord) {
            postUserInfo(mUserName , mPassWord);
            super.run();
        }
    }

    private void postUserInfo(final String mUserName, String mPassWord) {
        Log.e("登入URL",url);
        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"cAccount\":\""+mUserName+"\",\"cPassword\":\""+mPassWord+"\"}";
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
                        Toast.makeText(LoginActivity.this, "登入失敗 請重新輸入", Toast.LENGTH_SHORT).show();
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

            private void parseJson(String json) {
                String cStatus,cUserName,cUserID ;
                try {
                    cStatus = new JSONObject(json).getString("result");
                    if(cStatus.equals("1")){

                        cUserName = new JSONObject(json).getString("UserName");
                        cUserID = new JSONObject(json).getString("UserID");
                        Application.UserName = cUserName;
                        Application.UserID = cUserID;

                        Intent intent = new Intent(LoginActivity.this, AllListActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();

                        //記住帳號
                        SharedPreferences setting =
                                getSharedPreferences("Login", MODE_PRIVATE);
                        setting.edit()
                                .putString("userName", mUserName)
                                .commit();

                    }else if (cStatus.equals("0")){
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
        });

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
    //載入中
    private void setDialog(){
        myDialog = new ProgressDialog(LoginActivity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
        hideSystemNavigationBar();
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
}