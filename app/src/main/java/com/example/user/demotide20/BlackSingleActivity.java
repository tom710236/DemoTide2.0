package com.example.user.demotide20;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.user.demotide20.Application.cMark;
import static com.example.user.demotide20.R.id.button20;

public class BlackSingleActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {
    String url = Application.TideUrl+"Blank.aspx";
    String Abase64,Bbase64,Cbase64,Dbase64,Ebase64;
    Uri imgUri;    //用來參照拍照存檔的 Uri 物件
    Bitmap bmp;
    ArrayList Allbase64;
    int index,index2;

    SoundPool mSoundPool;
    int mSoundID ;

    String addNum;
    String Sname,Sname2;
    ArrayList mWarehouse;
    ArrayList mInputType;
    ArrayList mWarehouseNo;
    ArrayList mInputTypeNo;
    Object Warehouse;
    Object InputType;

    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    String cProductIDeSQL = "";
    ArrayList<LinkedHashMap<String, String>> myList;
    SpecialAdapter adapter;
    int addInt  = 1 ;
    boolean checkAdd ; //判斷是加一還是輸入數字
    boolean picInt ;   //判斷是否開啟工具模式
    boolean picAdd ;   //判斷是否開啟快速模式
    boolean iMatch ;   //判斷是否為第一次掃描或確定 (ex:每次掃描或確定都+1,然後快速模式+5 : 1+4 = 5 之後 每次都+5)

    String sType,sType2;
    //兩筆資料以上
    String[] stringArray;
    ArrayList  Btrans;
    LinkedHashMap<String, String> map,newMap,addMap;
    final String[] activity = {"換人檢", "結案"};

    String cProductName;
    ProgressDialog myDialog;

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    public  class InputTypeInfo {
        public String cTypeNO;
        public String cTypeName;

        InputTypeInfo(String TypeNO , String TypeName){
            this.cTypeNO = TypeNO;
            this.cTypeName = TypeName;
        }
        //方法
        public String toString() {
            //return this.cTypeName;
            return cTypeName;
        }

    }

    public  class WarehouseInfo {
        public String cTypeNO;
        public String cTypeName;

        WarehouseInfo(String TypeNO, String TypeName){
            this.cTypeNO = TypeNO;
            this.cTypeName = TypeName;
        }
        //方法
        public String toString() {

            return cTypeName;
        }

    }

    public  class InputTypeNoInfo {
        public String cTypeNO;
        public String cTypeName;

        InputTypeNoInfo(String TypeNO , String TypeName){
            this.cTypeNO = TypeNO;
            this.cTypeName = TypeName;
        }
        //方法
        public String toString() {
            //return this.cTypeName;
            return cTypeNO;
        }

    }

    public  class WarehouseNoInfo {
        public String cTypeNO;
        public String cTypeName;

        WarehouseNoInfo(String TypeNO, String TypeName){
            this.cTypeNO = TypeNO;
            this.cTypeName = TypeName;
        }
        //方法
        public String toString() {
            return cTypeNO;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_single);

        toolBar();
        setSwitch();
        PostGetType post = new PostGetType();
        post.start();
        setArraylist();
        setDialog();
        setKeyboard();
        hideSystemNavigationBar();
        setEditText();
        enterExitText();

        //音效宣告
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        mSoundPool.setOnLoadCompleteListener(BlackSingleActivity.this);
        mSoundID = mSoundPool.load (this, R.raw.windows_8_notify,1);


        Log.e("cMark2", cMark);
        if(Application.checkChange == true){
            Log.e("cMark", String.valueOf(Application.cMark.length()));
            if (cMark.length()>3){
                String[] token = Application.cMark.split(":");
                EditText editText = (EditText)findViewById(R.id.editText12);
                editText.setText(token[1]);
            }
            setHide();
            AllQty();
        }



    }

    private void toolBar() {
        Log.e("NAME2",Application.UserName);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(Application.UserName + "您好");

        //Toolbar 設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (Application.checkChange == false){
            //回到上一頁的圖示
            toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
            //回到上一頁按鍵設定
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BlackSingleActivity.this, AllListActivity.class);
                    startActivity(intent);
                    BlackSingleActivity.this.finish();
                }
            });
        }

    }

    public void onPicture(View v) {
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);
        frameLayout.setVisibility(View.GONE);
        LinearLayout linTop2 = (LinearLayout)findViewById(R.id.linTop2);

        linTop2.setVisibility(View.VISIBLE);
        LinTop.setVisibility(View.GONE);
        linear.setVisibility(View.GONE);
        linMid.setVisibility(View.GONE);
        linDown.setVisibility(View.GONE);
        list.setVisibility(View.GONE);

        ScrollView scrollView = (ScrollView)findViewById(R.id.ScroPic);
        scrollView.setVisibility(View.VISIBLE);

    }

    public void onBack (View v){
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);

        frameLayout.setVisibility(View.VISIBLE);
        LinTop.setVisibility(View.VISIBLE);
        linMid.setVisibility(View.VISIBLE);
        linDown.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);

        //如果快速模式打開 返回也要是打開的
        if(picAdd == true){
            LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
            Linear.setVisibility(View.VISIBLE);
        }else {
            LinearLayout Linear = (LinearLayout)findViewById(R.id.linear);
            Linear.setVisibility(View.GONE);
        }
        // 把照片存入
        Allbase64 = new ArrayList();
        if(Abase64 != null){
            Allbase64.add("\"" + Abase64 + "\"");
        }
        if(Bbase64 != null){
            Allbase64.add("\"" + Bbase64 + "\"");
        }
        if(Cbase64 != null){
            Allbase64.add("\"" + Cbase64 + "\"");
        }
        if(Dbase64 != null){
            Allbase64.add("\"" + Dbase64 + "\"");
        }
        if(Ebase64 != null){
            Allbase64.add("\"" + Ebase64 + "\"");
        }
    }
    //設定 Switch功能
    private void setSwitch() {
        //switch 設定
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        Switch sw = (Switch) findViewById(R.id.switch2);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //switch 點擊
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //點擊後 lineat就會顯示並且addNum=1
                if (isChecked) {
                    //顯示
                    linear.setVisibility(View.VISIBLE);
                    picAdd = true;
                }
                //沒有點擊 addNum=0
                else {
                    //關閉
                    linear.setVisibility(View.GONE);
                    picAdd = false;
                }

            }
        });
    }
    //加照片
    public void onPic1 (View v){
        setPicture(100);
    }
    public void onPic2(View v){
        setPicture(101);
    }
    public void onPic3(View v){
        setPicture(102);
    }
    public void onPic4(View v){
        setPicture(103);
    }
    public void onPic5(View v){
        setPicture(104);
    }
    //刪照片
    public void onDelAll (View v){
        ImageView imv,imv2,imv3,imv4,imv5;
        imv = (ImageView) findViewById(R.id.imageView14);
        imv.setImageResource(android.R.drawable.ic_menu_camera);
        imv2 = (ImageView) findViewById(R.id.imageView15);
        imv2.setImageResource(android.R.drawable.ic_menu_camera);
        imv3 = (ImageView) findViewById(R.id.imageView16);
        imv3.setImageResource(android.R.drawable.ic_menu_camera);
        imv4 = (ImageView) findViewById(R.id.imageView17);
        imv4.setImageResource(android.R.drawable.ic_menu_camera);
        imv5 = (ImageView) findViewById(R.id.imageView18);
        imv5.setImageResource(android.R.drawable.ic_menu_camera);
        Allbase64 = null;
    }
    public void onDel1 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView14);
        imv.setImageResource(android.R.drawable.ic_menu_camera);
        Abase64 = null ;
    }
    public void onDel2 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView15);
        imv.setImageResource(android.R.drawable.ic_menu_camera);
        Bbase64 = null ;
    }
    public void onDel3 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView16);
        imv.setImageResource(android.R.drawable.ic_menu_camera);
        Cbase64 = null ;
    }
    public void onDel4 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView17);
        imv.setImageResource(android.R.drawable.ic_menu_camera);
        Dbase64 = null ;
    }
    public void onDel5 (View v){
        ImageView imv;
        imv = (ImageView) findViewById(R.id.imageView18);
        imv.setImageResource(android.R.drawable.ic_menu_camera);
        Ebase64 = null ;
    }


    //拍照方法
    private void setPicture (int i){
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, i);
    }

    //拍照後的預覽畫面設定
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode==100) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==101) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==102) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==103) {
            showImg(requestCode);
        }else if(resultCode == Activity.RESULT_OK && requestCode==104) {
            showImg(requestCode);
        }
        else {
            Toast.makeText(this, "沒有拍到照片1", Toast.LENGTH_LONG).show();

        }
    }

    void showImg(int requestCode) {

        Log.e("showing", String.valueOf(imgUri));
        if(imgUri!=null){
            BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
            option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
            BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中

            if(requestCode == 100){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView14);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                AImgUriBase64(imgUri);
            }else if(requestCode == 101){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView15);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                BImgUriBase64(imgUri);
            }else if(requestCode == 102){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView16);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                CImgUriBase64(imgUri);
            }else if(requestCode == 103){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView17);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                DImgUriBase64(imgUri);
            }else if(requestCode == 104){
                ImageView imv;
                imv = (ImageView) findViewById(R.id.imageView18);
                option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
                option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
                bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
                imv.setImageBitmap(bmp);
                EImgUriBase64(imgUri);
            }
        }else{
            Toast.makeText(this, "沒有拍到照片2", Toast.LENGTH_LONG).show();

        }
    }

    // Uri 轉成Bitmap 再轉成 base64
    // bitmap 要轉成 jpg
    void AImgUriBase64(Uri uri) {
        Abase64 = Base64.encodeToString(setUrlToBase64(uri), Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Abase64 + "\"");
    }

    void BImgUriBase64(Uri uri) {
        Bbase64 = Base64.encodeToString(setUrlToBase64(uri), Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Bbase64 + "\"");
    }

    void CImgUriBase64(Uri uri) {
        Cbase64 = Base64.encodeToString(setUrlToBase64(uri), Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Cbase64 + "\"");
    }

    void DImgUriBase64(Uri uri) {
        Dbase64 = Base64.encodeToString(setUrlToBase64(uri), Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Dbase64 + "\"");
    }

    void EImgUriBase64(Uri uri) {
        Ebase64 = Base64.encodeToString(setUrlToBase64(uri), Base64.DEFAULT); // 把byte變成base64
        //Allbase64.add("\"" + Ebase64 + "\"");
    }


    // Uri 轉成Bitmap 再轉成 base64
    private byte[] setUrlToBase64 (Uri uri) {
        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔


        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte bytes[] = stream.toByteArray();

        return bytes;
    }

    class PostGetType extends Thread{
        @Override
        public void run() {
            PostGetTypeInfo();
        }
    }

    //Post getType
    private void PostGetTypeInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"gettype\"}";
        //Log.e("GetTypePOST",json);
        //Log.e("GetTypeURL",url);
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
                myDialog.dismiss();
                //非主執行緒顯示UI(Toast)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlackSingleActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
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
        String BlankType;
        InputType = new JSONArray();
        Warehouse = new JSONArray();

        mInputType = new ArrayList<>();
        mWarehouse = new ArrayList<>();

        mInputTypeNo = new ArrayList();
        mWarehouseNo = new ArrayList();

        mInputType.add("入庫分類");
        mWarehouse.add("庫別");

        mInputTypeNo.add("入庫分類");
        mWarehouseNo.add("庫別");


        try {
            JSONObject j = new JSONObject(json);
            BlankType= j.getString("BlankType");
            JSONObject j2 = new JSONObject(BlankType);
            for(int i=0 ; i< j2.getJSONArray("Warehouse").length();i++){
                //Warehouse = j2.getJSONArray("Warehouse").getJSONObject(i).get("Name");
                //mWarehouse.add(Warehouse);
                mWarehouse.add(new WarehouseInfo(String.valueOf(j2.getJSONArray("Warehouse").getJSONObject(i).get("No")), String.valueOf(j2.getJSONArray("Warehouse").getJSONObject(i).get("Name"))));
                mWarehouseNo.add(new WarehouseNoInfo(String.valueOf(j2.getJSONArray("Warehouse").getJSONObject(i).get("No")), String.valueOf(j2.getJSONArray("Warehouse").getJSONObject(i).get("Name"))));
            }
            for(int i2 = 0; i2<j2.getJSONArray("InputType").length();i2++){
                //InputType = j2.getJSONArray("InputType").getJSONObject(i2).get("Name");
                //mInputType.add(InputType);
                mInputType.add(new InputTypeInfo(String.valueOf(j2.getJSONArray("InputType").getJSONObject(i2).get("No")), String.valueOf(j2.getJSONArray("InputType").getJSONObject(i2).get("Name"))));
                mInputTypeNo.add(new InputTypeNoInfo(String.valueOf(j2.getJSONArray("InputType").getJSONObject(i2).get("No")), String.valueOf(j2.getJSONArray("InputType").getJSONObject(i2).get("Name"))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("Warehouse", String.valueOf(mWarehouse));
        Log.e("InputType", String.valueOf(mInputType));
        //把資料放進Spinner
        mSpinner(mInputType,mWarehouse);
    }
    //放進Spinner的方法
    private void mSpinner(final ArrayList mInputType, final ArrayList mWarehouse) {
        final Spinner SInputType = (Spinner) findViewById(R.id.spinner3);
        final ArrayAdapter<String> list = new ArrayAdapter<>(
                BlackSingleActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                mInputType);

        Log.e("Warehouse", String.valueOf(mWarehouse));
        Log.e("InputType", String.valueOf(BlackSingleActivity.this.mInputType));
        Log.e("index2222", String.valueOf(index));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myDialog.dismiss();
                SInputType.setAdapter(list);
                if(Application.checkChange == true){
                    SInputType.setSelection(Integer.parseInt(Application.cBlankType));
                }
            }
        });

        final Spinner SWarehouse = (Spinner) findViewById(R.id.spinner2);
        final ArrayAdapter<String> list2 = new ArrayAdapter<>(
                BlackSingleActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                mWarehouse);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myDialog.dismiss();
                SWarehouse.setAdapter(list2);
                if(Application.checkChange == true){
                    for(int i = 0 ; i<mWarehouseNo.size() ; i ++){
                        if(String.valueOf(mWarehouseNo.get(i)).equals(Application.cWHType)){
                            SWarehouse.setSelection(i);
                        }
                    }
                }
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
                sType = String.valueOf(mInputTypeNo.get(index));
                Log.e("index", String.valueOf(index));
                Log.e("sType", String.valueOf(sType));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });
        //spinner 點擊事件
        SWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //所點擊的索引值
                index2 = SWarehouse.getSelectedItemPosition();
                //所點擊的內容文字
                //Sname2 = SWarehouse.getSelectedItem().toString();
                sType2 = String.valueOf(mWarehouseNo.get(index2));
                Log.e("sType2", String.valueOf(sType2));



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });

    }

    public void enter (View v){
        final EditText editText = (EditText) findViewById(R.id.editText);
        Application.barcoode = editText.getText().toString();
        Log.e("條碼11",editText.getText().toString());
        cBarcode(editText.getText().toString());
        editText.requestFocus();
        editText.setText("");

    }
    //條碼比對 先無中文商品名稱
    private void cBarcode (String barcode){
        iMatch = false;
        addInt = 1 ;
        Btrans = new ArrayList();
        Log.e("條碼",barcode);
        //判斷是否有在條碼資料庫裡
        int iCheck = setBarcodeSQL(barcode);

        if (iCheck == 0 ){   //沒有 在條碼
            //判斷是否有在商品資料庫裡
            iCheck = setThingSQL(barcode);
            if (iCheck == 0){  //沒有 在商品  確認無此商品
                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                vibrator ();
            }else if(iCheck == 1){  //有一筆 在商品
                delMylist();
                Bsound();
                //判斷是否有在listView裡
                if (checkID(cProductIDeSQL) == true){ //有 在listView 增加數量
                    //判斷是+1還是NUM
                    if (checkAdd == false){
                        //直接+1
                        setNowQty(addInt,cProductIDeSQL,true);

                    }else {
                        //跳出輸入數量
                        setAlertDialog2(cProductIDeSQL,false);
                    }
                }else {  //沒有 在listView 建立商品
                    if(checkAdd== false){
                        addNewProduct(cProductIDeSQL,"1");
                    }else {
                        //跳出輸入數量
                        setAlertDialog2(cProductIDeSQL,true);
                    }

                }
            }else {  //有多筆 在商品
                if (checkID(cProductIDeSQL) == true){
                    Toast.makeText(this, "超過一筆資料1", Toast.LENGTH_SHORT).show();
                    stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                    chooseThings();
                    delMylist();
                }else {
                    stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                    chooseThings();
                    Toast.makeText(this, "超過一筆資料2", Toast.LENGTH_SHORT).show();
                    delMylist();
                }
            }
        }else if (iCheck == 1) {  //有一筆 在條碼裡
            delMylist();
            Bsound();
            //判斷是否有在listView裡
            if (checkID(cProductIDeSQL) == true) {  //有
                if (checkAdd == false) {
                    Log.e("條碼6",cProductIDeSQL);
                    setNowQty(addInt, cProductIDeSQL, true);
                } else {
                    Log.e("條碼7",cProductIDeSQL);
                    setAlertDialog2(cProductIDeSQL,false);
                }
            } else { //沒有
                if(checkAdd== false){
                    addNewProduct(cProductIDeSQL,"1");
                }else {
                    //跳出輸入數量
                    setAlertDialog2(cProductIDeSQL,true);

                }
            }

        }else {  //有多筆 在條碼裡
            //判斷是否有在listView裡
            if (checkID(cProductIDeSQL) == true){  //有
                Toast.makeText(this, "超過一筆資料3", Toast.LENGTH_SHORT).show();
                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                chooseThings();
                delMylist();
            }else {  //沒有
                Toast.makeText(this, "超過一筆資料4", Toast.LENGTH_SHORT).show();
                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                chooseThings();

                delMylist();
            }
        }

    }
    //商品條碼SQL
    private int setBarcodeSQL(String barcode) {
        helper4 = new MyDBhelper4(this, "tblTable4", null, 2);
        db4 = helper4.getWritableDatabase();

        Cursor c = db4.query("tblTable4",                          // 資料表名字
                null,                                              // 要取出的欄位資料
                "cBarcode = ? OR cProductID = ?",                  // 查詢條件式(WHERE)
                new String[]{barcode,barcode},                     // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductIDeSQL = c.getString(c.getColumnIndex("cProductID"));
            Log.e("條碼4", cProductIDeSQL);
            Btrans.add(cProductIDeSQL);

        }
        return c.getCount();

    }
    //商品清單SQL
    private int setThingSQL(String barcode) {
        helper = new MyDBhelper(this, DB_NAME, null, 2);
        db = helper.getWritableDatabase();

        Cursor c2 = db.query("tblTable",                           // 資料表名字
                null,                                              // 要取出的欄位資料
                "cProductID=?",                                    // 查詢條件式(WHERE)
                new String[]{barcode},                             // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c2.moveToNext()) {
            cProductIDeSQL = c2.getString(c2.getColumnIndex("cProductID"));
            Log.e("條碼2", cProductIDeSQL);
            Btrans.add(cProductIDeSQL);
        }
        return c2.getCount();
    }
    //判斷條碼內的商品是否有在list裡 有就回傳true
    private boolean checkID(String cProductIDeSQL) {
        for (int i = 0; i < myList.size(); i++) {
            if (cProductIDeSQL.equals(myList.get(i).get("ProductNo"))) {
                return true;
            }
        }
        return false;
    }
    //
    private void setArraylist(){
        myList = new ArrayList<>();
        map = new LinkedHashMap<String, String>();
        if (Application.checkChange == true){
            myList = Application.cMyList;
            Log.e("編輯後", String.valueOf(myList));

        }else {
            map.put("NowQty","11");
            map.put("ProductNo", "");
            map.put("cProductName", "");
            map.put("Qty", "");
            myList.add((LinkedHashMap<String, String>) map);
            Log.e("編輯後", String.valueOf(myList));
        }
        final ListView listView = (ListView) findViewById(R.id.list);
        adapter = new BlackSingleActivity.SpecialAdapter(
                BlackSingleActivity.this,
                myList,
                R.layout.lview9,
                new String[]{"cProductName", "ProductNo", "Qty"},
                new int[]{R.id.textView21, R.id.textView22, R.id.textView23});
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

    private void setNowQty (int addInt , String cProductIDeSQL, boolean ok){

            for (int i = 0; i< myList.size(); i++){
                if(cProductIDeSQL.equals(myList.get(i).get("ProductNo"))){
                    // 取出
                    //int i2 = Integer.parseInt(myList.get(i).get("NowQty"));
                    int i4 = Integer.parseInt(myList.get(i).get("Qty"));
                    Log.e("取出",i4+""+addInt+""+cProductIDeSQL);
                    //把改變的數量放入newMap 再置換 myList
                    newMap = new LinkedHashMap<String, String>();
                    //newMap.put("NowQty", String.valueOf(i4+addInt));
                    newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                    newMap.put("cProductName", myList.get(i).get("cProductName"));
                    newMap.put("Qty", String.valueOf(checkAddInt(i4, i4,addInt,false)));
                    //newMap.put("check",myList.get(i).get("check"));
                    //newMap.put("Sort",myList.get(i).get("Sort"));

                    myList.set(i, newMap);
                    adapter.notifyDataSetChanged();

                }

            }
        AllQty();
    }
    //設定輸入數量框
    private void setAlertDialog2(final String cID , final boolean checkNew){
        final View item = LayoutInflater.from(BlackSingleActivity.this).inflate(R.layout.item, null);
        new AlertDialog.Builder(BlackSingleActivity.this)
                .setTitle("請輸入數量")
                .setMessage("商品名稱:"+ checkName(cID) +"\r\n"+"商品編號:"+cID)
                .setView(item)
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //editText歸零
                        EditText editText = (EditText)findViewById(R.id.editText);
                        editText.setText("");
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) item.findViewById(R.id.editText2);
                        if (editText.length() != 0 && editText.length()<9) {
                            Log.e("數量",editText.getText().toString());
                            if (checkNew == false){
                                setNowQty(Integer.parseInt(editText.getText().toString()),cID,true);
                            }else {
                                addNum = editText.getText().toString();
                                addNewProduct(cProductIDeSQL,addNum);
                            }

                        }else {
                            Toast.makeText(BlackSingleActivity.this,"請輸入有效範圍數字",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).show();
    }

    private int checkAddInt (int i2 ,int i4 , int addInt , boolean ok){
        Log.e("數量I",i2 +","+ i4+","+ addInt) ;
        if (i2 + addInt >= i4 || addInt >= i4 ) {
            i2 = i2 + addInt ;
            if (ok == true){
                Toast.makeText(BlackSingleActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
            }
        }else {
            if(i2 + addInt < 0){
                i2 = 0;
            }else {
                i2 = i2 + addInt;
            }
        }
        return i2 ;
    }
    // 把一開始預設的空白列刪除掉 (只要listview 有資料)
    private void delMylist(){
        for (int i = 0 ; i<myList.size() ; i++){
            if(myList.get(i).get("NowQty") == "11"){
                myList.remove(i);
            }
        }
    }
    public void add1 (View v){
        int addInt = 1 ;
        if (cProductIDeSQL == null && cProductIDeSQL.equals("")){
            Log.e("addInt",cProductIDeSQL);
        }else {
            setNowQty(addInt,cProductIDeSQL,true);
            Log.e("addInt", String.valueOf(addInt));
        }
        iMatch = true;
    }
    public void add5 (View v){

        if(iMatch == true){
            addInt = 5 ;
        }else {
            addInt = 4 ;
            iMatch = true;
        }

        if (cProductIDeSQL == null && cProductIDeSQL.equals("")){
            Log.e("addInt",cProductIDeSQL);
        }else {
            setNowQty(addInt,cProductIDeSQL,true);
            Log.e("addInt", String.valueOf(addInt));
        }
    }
    public void add10 (View v){

        if(iMatch == true){
            addInt = 10 ;
        }else {
            addInt = 9 ;
            iMatch = true;
        }
        if (cProductIDeSQL == null && cProductIDeSQL.equals("")){
            Log.e("addInt",cProductIDeSQL);
        }else {
            setNowQty(addInt,cProductIDeSQL,true);
            Log.e("addInt", String.valueOf(addInt));
        }
    }
    public void addAll (View v){
        if(iMatch == true){
            addInt = 50 ;
        }else {
            addInt = 49 ;
            iMatch = true;
        }


        if (cProductIDeSQL == null && cProductIDeSQL.equals("")){
            Log.e("addInt",cProductIDeSQL);
        }else {
            setNowQty(addInt,cProductIDeSQL,true);
            Log.e("addInt", String.valueOf(addInt));
        }
        iMatch = true;
    }
    //動作按鍵
    public void onActivity(View v) {
        chooseActivity();
        Application.checkChange = false;
    }
    //動作按鍵的方法 (選擇檢貨或換人檢)
    private void chooseActivity() {
        /*
        if(getPickupProducts().length()>0&&index>0&&index2>0){
            PostendInfo();
        }else {
            Toast.makeText(BlackSingleActivity.this, "資料填寫不完整", Toast.LENGTH_SHORT).show();
        }
        */
        setDialog();
        PostendInfo();

    }

    //從myList取出ProductNo NowQty 放入upList POST用
    private String getPickupProducts(){
        Map<String, String> upMap;
        ArrayList<LinkedHashMap<String, String>>
                upList = new ArrayList<LinkedHashMap<String, String>>();
        for (int i = 0; i < myList.size(); i++) {
            //LinkedHashMap<String, String>() 會依照put的順序
            upMap = new LinkedHashMap<String, String>();
            upMap.put("\"ProductNo\"", "\"" + myList.get(i).get("ProductNo") + "\"");
            upMap.put("\"Qty\"", myList.get(i).get("Qty"));
            upList.add((LinkedHashMap<String, String>) upMap);
        }
        //Log.e("upList", String.valueOf(upList));
        String upString = String.valueOf(upList).replaceAll("=", ":");
        String upStringList = upString.replaceAll(", ", ",");
        //Log.e("upStringList", String.valueOf(upStringList));
        return upStringList;
    }
    //POST成功後取得1跳回前一頁
    private void changeEnd(String json) {
        String result = null;
        try {
            result = new JSONObject(json).getString("result");
        } catch (final JSONException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BlackSingleActivity.this, String.valueOf(e) , Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (result.equals("1")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BlackSingleActivity.this, "空白表單已儲存", Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(BlackSingleActivity.this, AllListActivity.class);
            startActivity(intent);
            BlackSingleActivity.this.finish();
        }else {
            Intent it = new Intent(BlackSingleActivity.this,BlackSingleActivity.class);
            startActivity(it);
            BlackSingleActivity.this.finish();
        }
    }

    private void PostendInfo() {
        if(Application.checkChange == true){
            sType = Application.cBlankType;
            sType2 = Application.cWHType;
        }
        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        //String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"PickupNumbers\" :\"" + Application.check3 + "\",\"PickupProducts\":" + getPickupProducts() + ",\"imgbase64\": " + Allbase64 + "}";
        String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"BlankInfo\":{\"BlankType\":"+"\""+sType+"\",\"WHType\":"+"\""+sType2+"\",\"UserID\":\""+Application.UserID+"\",\"Remark\":\"" + getPs() + "\",\"BlankNo\":\"" + getBlankNo() + "\",\"BlankProduct\":"+getPickupProducts()+"},\"imgbase64\": "+Allbase64+"}";
        //Log.e("sType222",sType);
        Log.e("結案POST", json);
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
                //
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myDialog.dismiss();
                        Toast.makeText(BlackSingleActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                String json = response.body().string();
                myDialog.dismiss();
                Log.e("結案後POST的回傳值", json);
                Log.e("結案URL",url);
                changeEnd(json);
            }
        });
    }

    private void setDialog(){
        myDialog = new ProgressDialog(BlackSingleActivity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }
    //輸入的條碼 有兩個以上商品 跳出對話框 選擇商品
    private void chooseThings() {
        Bsound1();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請選擇商品編號");
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("點擊", stringArray[i]);

                //判斷是否有在listview裡
                if(checkID(stringArray[i])==true){ // 有在listview裡
                    //判斷是+1還是NUM
                    if (checkAdd == false){ //加1
                        Log.e("點擊2", stringArray[i]);
                        setNowQty(addInt,stringArray[i],true);
                    }else { // NUM
                        setAlertDialog2(cProductIDeSQL,false);
                    }
                }else { // 不在listview
                    if(checkAdd== false){ //加1
                        Log.e("點擊21", stringArray[i]);
                        addNewProduct(stringArray[i],"1");
                    }else { //NUM
                        //跳出輸入數量
                        Log.e("點擊22", stringArray[i]);
                        setAlertDialog2(cProductIDeSQL,true);
                    }
                }
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void setKeyboard(){
        final EditText editText = (EditText) findViewById(R.id.editText);
        //Android 對 EditText 取得 focus
        editText.requestFocus();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    //三鍵隱藏
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

    private String getPs(){
        EditText editText = (EditText)findViewById(R.id.editText12);

        return editText.getText().toString();
    }
    private String getBlankNo(){
        String BlankNo;
        if(Application.checkChange == true){
            BlankNo = Application.cBlankNo;
        }else {
            BlankNo = "";
        }
        return BlankNo;
    }
    // 判斷是否要+1還是輸入數量 (NUM or +1 )
    public void onClickAdd (View v){
        if(checkAdd==true){
            checkAdd = false ;
        }else {
            checkAdd = true ;
        }
        if(checkAdd == false){
            Button button = (Button)findViewById(button20);
            button.setText("+1");
        }else {
            Button button = (Button)findViewById(button20);
            button.setText("NUM");
        }
    }
    private void addNewProduct (String cProductIDeSQL2 ,String Qty){
        //一開始建立假如輸入小於0 就為0
        if(Integer.parseInt(Qty) < 0){
            Qty = "0";
        }
        Log.e("cProductIDeSQL22",cProductIDeSQL2);
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        db = helper.getWritableDatabase();

        Cursor c = db.query("tblTable",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                "cProductID=?",                                    // 查詢條件式(WHERE)
                new String[]{cProductIDeSQL2},                       // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductName = c.getString(c.getColumnIndex("cProductName"));
            Log.e("cProductName", cProductName);
            addMap = new LinkedHashMap<>();
            addMap.put("cProductName", cProductName);
            addMap.put("ProductNo", cProductIDeSQL2);
            addMap.put("Qty", Qty);
            myList.add(addMap);
            adapter.notifyDataSetChanged();
        }
        Log.e("點擊數量", String.valueOf(c.getCount()));
        if (c.getCount() ==0){
            Toast.makeText(this,"商品資料庫有誤",Toast.LENGTH_SHORT).show();
        }
        AllQty();
    }

    private void setEditText(){

        final EditText editText = (EditText) findViewById(R.id.editText);
        EditText editText1 = (EditText)findViewById(R.id.editText9);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*
                if(editText.getText().length()>=13){
                    Application.barcoode = editText.getText().toString();
                    cBarcode(editText.getText().toString());
                    editText.requestFocus();
                    editText.setText("");

                }
                    */
                editText.requestFocus();
                return false;
            }
        });
        editText1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*
                if(editText.getText().length()>=13){
                    Application.barcoode = editText.getText().toString();
                    cBarcode(editText.getText().toString());
                    editText.requestFocus();
                    editText.setText("");

                }
                    */
                editText.requestFocus();
                return false;
            }
        });

    }
    //虛擬鍵盤按下enter
    private void enterExitText(){
        final EditText editText = (EditText)findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                cBarcode(editText.getText().toString());
                editText.setText("");
                editText.requestFocus();
                return false;
            }
        });

    }
    private void setHide(){
        Button button = (Button)findViewById(R.id.button20);
        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton2);
        TextView textView = (TextView)findViewById(R.id.textView3);
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linMid2 = (LinearLayout)findViewById(R.id.linMid2);
        LinearLayout linMid3 = (LinearLayout)findViewById(R.id.linMid3);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);
        Spinner spinner = (Spinner)findViewById(R.id.spinner2);
        Spinner spinner1 = (Spinner)findViewById(R.id.spinner3);
        Button button1 = (Button)findViewById(R.id.button24);
        EditText editText = (EditText)findViewById(R.id.editText3);
        TextView textView1 = (TextView)findViewById(R.id.textView13);
        EditText editText1 = (EditText)findViewById(R.id.editText);
        Button button2 = (Button)findViewById(R.id.button6);
        LinearLayout linIn = (LinearLayout)findViewById(R.id.linIn);
        TextView textView2 = (TextView)findViewById(R.id.textView52);

        textView2.setVisibility(View.VISIBLE);
        linIn.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        editText1.setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);

        button1.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        spinner1.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
        linMid.setVisibility(View.VISIBLE);
        linMid2.setVisibility(View.VISIBLE);
        linMid3.setVisibility(View.GONE);
        linDown.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);


        button.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);

    }

    public void onHide(View v){
        if(getPickupProducts().length()>0&&index>0&&index2>0){
            setHide();
        }else {
            Toast.makeText(BlackSingleActivity.this, "資料填寫不完整", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickPic (View v){
        if(picInt == true){
            LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
            LinTop.setVisibility(View.GONE);
            picInt = false;
        }else{
            LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
            LinTop.setVisibility(View.VISIBLE);
            picInt = true;
        }
    }
    //設定 返回鍵 和 拍照鍵 (硬體按鍵)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_CAMERA) { // 攔截返回鍵
            return true;
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }
    //音效 短 (有找到)
    private void Bsound(){
        mSoundPool.play(mSoundID,1.0F,1.0F,0,0,0.0f);
    }
    //音效 長 (雙商品)
    private void Bsound1(){
        mSoundPool.play(mSoundID,1.0F,1.0F,0,0,1.0f);
    }
    //手機震動
    private void vibrator (){
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(1500);
    }
    private String checkName (String cID){
        helper = new MyDBhelper(this, DB_NAME, null, 1);
        db = helper.getWritableDatabase();

        Cursor c = db.query("tblTable",                            // 資料表名字
                null,                                              // 要取出的欄位資料
                "cProductID=?",                                    // 查詢條件式(WHERE)
                new String[]{cID},                                 // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)

        while (c.moveToNext()) {
            cProductName = c.getString(c.getColumnIndex("cProductName"));
        }
        return cProductName;
    }

    private void AllQty (){
        int Qty = 0;
        int AllQty = 0 ;
            for (int i = 0 ; i<myList.size() ; i++){
                Qty = Integer.parseInt(myList.get(i).get("Qty"));
                AllQty = AllQty + Qty ;
            }
        Log.e("總量", String.valueOf(AllQty));
        TextView textView = (TextView)findViewById(R.id.textView52);
        textView.setText(String.valueOf(AllQty));
    }

}
