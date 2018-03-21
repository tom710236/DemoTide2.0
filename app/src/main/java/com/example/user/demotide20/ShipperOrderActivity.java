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
import android.graphics.Color;
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
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

import static com.example.user.demotide20.R.id.checkBox4;
import static com.example.user.demotide20.R.id.checkBox5;
import static com.example.user.demotide20.R.id.textView21;
import static com.example.user.demotide20.R.id.textView23;
import static com.example.user.demotide20.R.id.textView24;
import static com.example.user.demotide20.R.id.textView49;
import static com.example.user.demotide20.R.layout.lview4;

public class ShipperOrderActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {
    // 新增數量
    int addInt  = 1 ;
    String cProductIDeSQL = "";
    String url = Application.TideUrl + "Pickup.aspx";
    MyDBhelper helper;
    MyDBhelper4 helper4;
    SQLiteDatabase db, db4;
    final String DB_NAME = "tblTable";
    LinkedHashMap<String, String> map;
    ArrayList<LinkedHashMap<String, String>> myList;
    SpecialAdapter adapter;
    String Abase64, Bbase64, Cbase64, Dbase64, Ebase64;
    LinkedHashMap<String, String> newMap;
    Uri imgUri;    //用來參照拍照存檔的 Uri 物件
    Bitmap bmp;
    boolean checkGift = false;
    boolean checkEnter = false;
    //兩筆資料以上
    String[] stringArray;
    ArrayList  Btrans;

    //checkbox勾勾
    CheckBox checkbox;

    ProgressDialog myDialog;
    final String[] activity = {"換人檢", "結案"};

    ArrayList  Allbase64;
    SoundPool mSoundPool;
    private HashSet<Integer> mCheckSet = new HashSet<Integer>();
    boolean checkAdd ; //判斷是加一還是輸入數字
    boolean picInt ;   //判斷是否開啟工具模式
    boolean picAdd ;   //判斷是否開啟快速模式
    boolean iMatch ;   //判斷是否為第一次掃描或確定 (ex:每次掃描或確定都+1,然後快速模式+5 : 1+4 = 5 之後 每次都+5)

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }
    int mSoundID ;


    //建構子
    public class ProductIDInfo {
        private String mProductID;

        ProductIDInfo(String ProductID) {
            this.mProductID = ProductID;
        }

        public String toString() {
            return mProductID;
        }
    }

    public class ProductNameInfo {
        private String mProductName;

        ProductNameInfo(String ProductID) {
            this.mProductName = ProductID;
        }

        public String toString() {
            return mProductName;
        }
    }

    public class QtyInfo {
        private String mmQty;

        QtyInfo(String ProductID) {
            this.mmQty = ProductID;
        }

        public String toString() {
            return mmQty;
        }
    }

    public class SortInfo {
        private String mmSort;

        SortInfo(String ProductID) {
            this.mmSort = ProductID;
        }

        public String toString() {
            return mmSort;
        }
    }

    public class NowQtyInfo {
        private String mNowQty;

        NowQtyInfo(String ProductID) {
            this.mNowQty = ProductID;
        }

        public String toString() {
            return mNowQty;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order);
        //Post後listview顯示資料
        Post post = new Post();
        post.start();
        setSwitch();
        setEditText();
        setCheckBox();
        setDialog();
        enterExitText();
        //音效宣告
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        mSoundPool.setOnLoadCompleteListener(ShipperOrderActivity.this);
        mSoundID = mSoundPool.load (this, R.raw.windows_8_notify,1);

        Intent intent = this.getIntent();
        checkGift = intent.getBooleanExtra("checkGift",true);
        Log.e("贈品", String.valueOf(checkGift));

        hideSystemNavigationBar();
        if (checkGift == true){
            TextView textView = (TextView)findViewById(R.id.textView11);
            textView.setText("世潮檢貨系統"+"("+"含贈品"+")"+Application.upDate);
        }else{
            TextView textView = (TextView)findViewById(R.id.textView11);
            textView.setText("世潮檢貨系統"+Application.upDate);
        }
        setKeyboard();

    }

    class Post extends Thread {

        String cProductName;
        String cSort;

        @Override
        public void run() {
            PostOrderThingsInfo();
        }

        private void PostOrderThingsInfo() {
            OkHttpClient client = new OkHttpClient();
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"Token\":\"\" ,\"Action\":\"dopickups\",\"UserID\":\"" + Application.UserID + "\",\"PickupNumbers\":\"" + Application.check3 + "\"}";
            Log.e("POST的JSON", json);
            Log.e("POST的URL", url);
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
                    myDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();
                    Log.e("回傳的JSON", json);
                    parseJson(json);
                }
            });
        }

        private void parseJson(String json) {
            myList = new ArrayList<LinkedHashMap<String, String>>();
            //比對資料庫資料
            try {
                JSONObject j = new JSONObject(json);
                for (int i = 0; i < j.getJSONArray("PickUpProducts").length(); i++) {
                    JSONObject obj = j.getJSONArray("PickUpProducts").getJSONObject(i);

                    helper = new MyDBhelper(ShipperOrderActivity.this, DB_NAME, null, 7);
                    db = helper.getWritableDatabase();

                    Cursor c = db.query("tblTable",                            // 資料表名字
                            null,                                              // 要取出的欄位資料
                            "cProductID = ? ",                                 // 查詢條件式(WHERE)
                            new String[]{obj.optString("ProductNo")},          // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                            null,                                              // Group By字串語法
                            null,                                              // Having字串法
                            null);                                             // Order By字串語法(排序)

                    while (c.moveToNext()) {
                        cProductName = c.getString(c.getColumnIndex("cProductShortName"));  //商品名稱顯示改變
                        Log.e("cProductName", cProductName);
                        cSort = c.getString(c.getColumnIndex("cSort"));  //商品名稱顯示改變
                        Log.e("cSort", cSort);
                    }

                    //用自訂類別 把JSONArray的值取出來

                    map = new LinkedHashMap<String, String>();
                    map.put("NowQty", String.valueOf(new NowQtyInfo(obj.optString("NowQty"))));
                    map.put("ProductNo", String.valueOf(new ProductIDInfo(obj.getString("ProductNo"))));
                    map.put("cProductName", String.valueOf(new ProductNameInfo(cProductName)));
                    map.put("Qty", String.valueOf(new QtyInfo(obj.getString("Qty"))));
                    map.put("Sort", String.valueOf(new SortInfo(cSort)));
                    map.put("check", "0");
                    map.put("checkEnter","0");
                    myList.add(map);
                    db.close();

                    Log.e("map", String.valueOf(map));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setListView();

        }
    }


    private void setListView() {
        final ListView listView = (ListView) findViewById(R.id.list);
        adapter = new SpecialAdapter(
                ShipperOrderActivity.this,
                myList,
                lview4,
                new String[]{"cProductName", "ProductNo", "Qty", "NowQty", "checkbox","Sort"},
                new int[]{textView21, R.id.textView22, textView23, textView24, checkBox4, textView49});

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                checkListArray();

            }
        });

        myDialog.dismiss();

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
                TextView textView21 = (TextView) view.findViewById(R.id.textView21);
                textView21.setTextColor(Color.BLACK);
                TextView textView22 = (TextView) view.findViewById(R.id.textView22);
                textView22.setTextColor(Color.BLACK);
                TextView textView23 = (TextView) view.findViewById(R.id.textView23);
                textView23.setTextColor(Color.BLACK);
                TextView textView24 = (TextView) view.findViewById(R.id.textView24);
                textView24.setTextColor(Color.BLACK);

                checkbox = (CheckBox) view.findViewById(R.id.checkBox4);
                //checkBox.setChecked(mCheckSet.contains(position));

                //數量=總量時便item變顏色
                for (int i = 0; i < myList.size(); i++) {
                    if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {

                        if (position == i) {
                            //view.setBackgroundColor(colors2);
                            //return view;
                            textView21.setTextColor(colors2);
                            textView22.setTextColor(colors2);
                            textView23.setTextColor(colors2);
                            textView24.setTextColor(colors2);
                        }

                    }

                }
                setCheckBoxOne(position);
            }
            return view;
        }
    }

    public void onClickPic (View v){
        if(picInt == true){
            LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
            LinTop.setVisibility(View.VISIBLE);
            picInt = false;
        }else{
            LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
            LinTop.setVisibility(View.GONE);
            picInt = true;
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
    //拍照按鍵 切換到拍照頁面
    public void onPicture(View v) {
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        EditText editText7 = (EditText)findViewById(R.id.editText7);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);
        frameLayout.setVisibility(View.GONE);

        LinTop.setVisibility(View.GONE);
        linear.setVisibility(View.GONE);
        linMid.setVisibility(View.GONE);
        linDown.setVisibility(View.GONE);
        editText7.setVisibility(View.GONE);
        list.setVisibility(View.GONE);

    }

    public void onBack (View v){
        LinearLayout LinTop = (LinearLayout)findViewById(R.id.LinTop);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        LinearLayout linMid = (LinearLayout)findViewById(R.id.linMid);
        LinearLayout linDown = (LinearLayout)findViewById(R.id.linDown);
        EditText editText7 = (EditText)findViewById(R.id.editText7);
        ListView list = (ListView)findViewById(R.id.list);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.FrameLayout);

        frameLayout.setVisibility(View.VISIBLE);
        LinTop.setVisibility(View.VISIBLE);
        linMid.setVisibility(View.VISIBLE);
        //linear.setVisibility(View.INVISIBLE);
        linDown.setVisibility(View.VISIBLE);
        editText7.setVisibility(View.VISIBLE);
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

    private void setEditText(){
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(editText.getText().length()>=13){
                    Application.barcoode = editText.getText().toString();
                    cBarcode(editText.getText().toString());
                    editText.requestFocus();
                    editText.setText("");
                }

                return false;
            }
        });
    }

    //判斷條碼

    /**
     * 1.輸入條碼
     * 2.開啟條碼資料庫
     * 3.比對所輸入的條碼 有無符合條碼資料庫的 cBarcode , cProductID
     *      沒有 - 比對所輸入的條碼 有無符合商品清單資料庫的 cProductID
     *          沒有 - 查無商品
     *          有一筆 - 判斷是否有在listView
     *                沒有 - 查無商品
     *                  有 - 判斷是+1還是NUM
     *                       +1 - setNowQty(addInt,cProductIDeSQL,true); // 直接+1
     *                        NUM - setAlertDialog(cProductIDeSQL);       // 跳出輸入數量dialog
     *          有多筆 - 判斷是否有在listView
     *                 沒有 - 查無商品
     *                   有 - 跳出對話框 選擇商品
     *                        判斷是+1還是NUM
     *      有一筆 - 判斷是否有在listView
     *          沒有 - 查無商品
     *          有一筆 - 判斷是否有在listView
     *                沒有 - 查無商品
     *                  有 - 判斷是+1還是NUM
     *                       +1 - setNowQty(addInt,cProductIDeSQL,true); // 直接+1
     *                        NUM - setAlertDialog(cProductIDeSQL);       // 跳出輸入數量dialog
     *          有多筆 - 判斷是否有在listView
     *                 沒有 - 查無商品
     *                   有 - 跳出對話框 選擇商品
     *                        判斷是+1還是NUM
     *
     */

    private void cBarcode(String barcode){

        iMatch = false;
        addInt = 1 ;
        Btrans = new ArrayList();
        Log.e("條碼",barcode);


        if("".equals(barcode.trim())){

        }else{
            int iCheck = setBarcodeSQL(barcode);
            Log.e("ICHECK", String.valueOf(iCheck));
            //icheck == 0 表示 條碼資料庫 比對 cBarcode cProductID 無資料
            if(iCheck == 0){
                //開啟商品清單資料庫 比對 cProductID
                iCheck = setThingSQL(barcode);
                Log.e("ICHECK2", String.valueOf(iCheck));
                //iCheck == 0 表示商品清單資料庫 比對 cProductID 無資料
                if (iCheck == 0 ){
                    Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                    Bsound1();
                    focusEdit();
                    //iCheck == 1 表示商品清單資料庫 比對 cProductID 有一筆資料
                }else if (iCheck == 1){
                    //判斷是否有在listView裡
                    if (checkID(cProductIDeSQL) == true){
                        Bsound();
                        //Toast.makeText(this, cProductIDeSQL , Toast.LENGTH_SHORT).show();
                        focusEdit();
                        //判斷是+1還是NUM
                        if (checkAdd == false){
                            //直接+1
                            setNowQty(addInt,cProductIDeSQL,true);
                            Log.e("setNowQty","1");
                        }else {
                            //跳出輸入數量
                            setAlertDialog(cProductIDeSQL);
                        }
                        //沒有在listView
                    }else {

                        Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                        Bsound1();
                        focusEdit();
                    }
                    //表示商品清單資料庫 比對 cProductID 有多筆資料
                }else {

                    stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                    check2ProductNo();
                    //判斷是否有在listView裡
                /*
                if (checkID(cProductIDeSQL) == true){
                    Bsound();
                    Toast.makeText(this, "超過一筆資料", Toast.LENGTH_SHORT).show();
                    stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                    chooseThings();
                }else{
                    Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                    Bsound1();
                }
                */

                }
                //icheck == 0 表示 條碼資料庫 比對 cBarcode cProductID 有一筆資料
            }else if (iCheck == 1){
                //判斷是否有在listView裡
                if (checkID(cProductIDeSQL) == true){ //有
                    Bsound();
                    //Toast.makeText(this, cProductIDeSQL, Toast.LENGTH_SHORT).show();
                    focusEdit();
                    //判斷是+1還是NUM
                    if (checkAdd == false){
                        setNowQty(addInt,cProductIDeSQL,true);
                        Log.e("setNowQty","2");
                    }else {
                        setAlertDialog(cProductIDeSQL);
                    }
                }else { //沒有
                    Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                    Bsound1();
                    focusEdit();
                }
                //表示 條碼資料庫 比對 cBarcode cProductID 有多筆資料
            }else {

                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                check2ProductNo();


                //判斷是否有在listView裡
            /*
            if (checkID(cProductIDeSQL) == true){
                Bsound();
                Toast.makeText(this, "超過一筆資料", Toast.LENGTH_SHORT).show();
                stringArray = (String[]) Btrans.toArray(new String[Btrans.size()]);
                chooseThings();
            }else {
                Toast.makeText(this, "查無商品1", Toast.LENGTH_SHORT).show();
            }
                */
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
            Log.e("條碼1", cProductIDeSQL);
            Btrans.add(cProductIDeSQL);

        }
        return c.getCount();
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


    private void setNowQty (int addInt , String cProductIDeSQL, boolean ok) {
        if(checkfull(addInt,cProductIDeSQL) == true){
            vibrator();
            Toast.makeText(ShipperOrderActivity.this, "數量已超過訂單數量", Toast.LENGTH_SHORT).show();
            focusEdit();
            checkListArray();
            checkListArray3(cProductIDeSQL);
            adapter.notifyDataSetChanged();

        }else {
            for (int i = 0; i < myList.size(); i++) {
                if (cProductIDeSQL.equals(myList.get(i).get("ProductNo"))) {
                    // 取出
                    int i2 = Integer.parseInt(myList.get(i).get("NowQty"));
                    int i4 = Integer.parseInt(myList.get(i).get("Qty"));

                    //把改變的數量放入newMap 再置換 myList
                    newMap = new LinkedHashMap<String, String>();
                    newMap.put("NowQty", String.valueOf(checkAddInt(i2, i4, addInt, ok)));
                    newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                    newMap.put("cProductName", myList.get(i).get("cProductName"));
                    newMap.put("Qty", myList.get(i).get("Qty"));
                    newMap.put("check", myList.get(i).get("check"));
                    newMap.put("Sort", myList.get(i).get("Sort"));
                    newMap.put("checkEnter", "1");
                    myList.set(i, newMap);

                    new CreateFoler().extelnalPrivateCreateFoler(getExtermalStoragePrivateDir("Log"), Application.barcoode, myList.get(i).get("cProductName"), myList.get(i).get("ProductNo"), myList.get(i).get("Qty"), String.valueOf(checkAddInt(i2, i4, addInt, false)), String.valueOf(addInt));

                    adapter.notifyDataSetChanged();
                    checkListArray();
                    checkListArray4(cProductIDeSQL);

                }
            }
        }
        // listview 跳到第一項
        ListView list = (ListView)findViewById(R.id.list);
        list.setSelection(0);
    }
    public void enter (View v){

        final EditText editText = (EditText) findViewById(R.id.editText);
        Application.barcoode = editText.getText().toString();
        //判斷edittext是否為空白
        if("".equals(editText.getText().toString().trim())){
            Log.e ("空白","空白");
        }else {
            cBarcode(editText.getText().toString());
            editText.requestFocus();
            editText.setText("");
        }

    }

    private int checkAddInt (int i2 ,int i4 , int addInt , boolean ok){
        Log.e("數量I",i2 +","+ i4+","+ addInt) ;
        if (i2 + addInt >= i4 || addInt >= i4 ) {
            if (ok == true){
                //vibrator();
                //Toast.makeText(ShipperOrderActivity.this, "數量已滿", Toast.LENGTH_SHORT).show();
                //focusEdit();
            }
            i2 = i4;
        }else {
            if(i2 + addInt < 0){
                i2 = 0;
            }else {
                i2 = i2 + addInt;
            }
        }
        return i2 ;
    }

    // 判斷是否要+1還是輸入數量 (NUM or +1 )
    public void onClickAdd (View v){
        if(checkAdd==true){
            checkAdd = false ;
        }else {
            checkAdd = true ;
        }
        if(checkAdd == false){
            Button button = (Button)findViewById(R.id.button20);
            button.setText("+1");
        }else {
            Button button = (Button)findViewById(R.id.button20);
            button.setText("NUM");
        }
    }

    //設定輸入數量框
    private void setAlertDialog(final String cID){
        final View item = LayoutInflater.from(ShipperOrderActivity.this).inflate(R.layout.item, null);
        new AlertDialog.Builder(ShipperOrderActivity.this)
                .setTitle("請輸入數量")
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
                        if (editText.length() != 0) {
                            Log.e("數量",editText.getText().toString());
                            setNowQty(Integer.parseInt(editText.getText().toString()),cID,true);
                            Log.e("setNowQty","3");
                        }
                        hideSystemNavigationBar();
                    }
                }).show();
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
        int addInt = 999999 ;
        if (cProductIDeSQL == null && cProductIDeSQL.equals("")){
            Log.e("addInt",cProductIDeSQL);
        }else {
            setNowQty(addInt,cProductIDeSQL,true);
            Log.e("addInt", String.valueOf(addInt));
        }
        iMatch = true;
    }

    //checkBox若勾選 則檢貨數量=訂單數量
    private void setCheckBoxOne (final int position){

        checkbox.setChecked(mCheckSet.contains(position));
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //沒有勾勾的時候點擊
                int Qty = Integer.parseInt(myList.get(position).get("Qty"));
                int NowQty = Integer.parseInt(myList.get(position).get("NowQty"));
                int addint = Qty-NowQty;
                Log.e ("CHECKNUM",Qty+","+NowQty+","+addint);

                if (((CheckBox) v).isChecked()) {
                    if(myList.get(position).get("NowQty").equals(myList.get(position).get("Qty"))){
                        setNowQty(-addint,myList.get(position).get("ProductNo"),false);
                        adapter.notifyDataSetChanged();
                        checkbox.setChecked(false);
                    }else{
                        setNowQty(addint,myList.get(position).get("ProductNo"),false);
                        adapter.notifyDataSetChanged();
                        checkbox.setChecked(false);
                    }
                    //有勾勾的時候點擊
                }else {
                    if (myList.get(position).get("NowQty").equals(myList.get(position).get("Qty"))) {
                        setNowQty(-Integer.parseInt(myList.get(position).get("Qty")),myList.get(position).get("ProductNo"),false);
                        adapter.notifyDataSetChanged();
                        checkbox.setChecked(false);
                        Log.e("checkBox", "數量滿");

                    } else {
                        setNowQty(Integer.parseInt(myList.get(position).get("Qty")),myList.get(position).get("ProductNo"),false);
                        adapter.notifyDataSetChanged();
                        checkbox.setChecked(false);
                        Log.e("checkBox2", myList.get(position).get("NowQty"));
                        Log.e("checkBox3", myList.get(position).get("Qty"));
                    }
                }
                checkListArray();
                checkListArray2();

            }
        });
    }

    //檢完和沒檢完的排序
    private void checkListArray() {
        //先依照商品名稱排序
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("ProductNo"));
                String value2 = (o2.get("ProductNo"));

                return value1.compareTo(value2);

            }

        });
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("Sort"));
                String value2 = (o2.get("Sort"));

                return value1.compareTo(value2);

            }

        });

        // 如果檢完貨 新添加的欄位(check)就等於sort(方便下一次排序)
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {

                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("Sort",myList.get(i).get("Sort"));
                newMap.put("check", myList.get(i).get("Sort"));
                newMap.put("checkEnter",myList.get(i).get("checkEnter"));
                myList.set(i, newMap);

            } else {
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("Sort",myList.get(i).get("Sort"));
                newMap.put("check", "0");
                newMap.put("checkEnter",myList.get(i).get("checkEnter"));
                myList.set(i, newMap); // 替換
            }
        }


        //排序check (及撿完貨的排序)
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("check"));
                String value2 = (o2.get("check"));

                return value1.compareTo(value2);

            }

        });

        /*
        for(int i2 = 0; i2 < myList.size(); i2++) {
            for (int i = 0; i < myList.size(); i++) {
                final LinkedHashMap<String, String> item = myList.get(i);
                int size = myList.size() - 1;
                Log.e("myList清單", String.valueOf(myList.get(i)));
                if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                    //Log.e("list", String.valueOf(myList.get(i)));
                    Log.e("item", String.valueOf(item));
                    //myList2.add(0,item);
                    myList.remove(i);

                    myList.add(size, item);

                } else {

                }
            }
        }
        */


        // 排序完後 檢完貨的位置 假如檢完 勾勾留著
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                mCheckSet.add(i);
            } else {
                mCheckSet.remove(i);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CheckBox checkBox = (CheckBox) findViewById(checkBox5);
                if(mCheckSet.size()==myList.size()){
                    checkBox.setChecked(true);
                }else {
                    checkBox.setChecked(false);
                }
            }
        });


    }

    //在檢的時候 排序在最上面
    private void checkListArray2() {

        //先依照商品名稱排序
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("checkEnter"));
                String value2 = (o2.get("checkEnter"));
                return value2.compareTo(value1);

            }

        });
        for (int i = 0 ; i<myList.size() ; i++){
            newMap = new LinkedHashMap<String, String>();
            newMap.put("NowQty", myList.get(i).get("NowQty"));
            newMap.put("ProductNo", myList.get(i).get("ProductNo"));
            newMap.put("cProductName", myList.get(i).get("cProductName"));
            newMap.put("Qty", myList.get(i).get("Qty"));
            newMap.put("Sort",myList.get(i).get("Sort"));
            newMap.put("check", myList.get(i).get("check"));
            newMap.put("checkEnter","0");
            myList.set(i, newMap); // 替換

        }
        // 如果檢完貨 新添加的欄位(check)就等於sort(方便下一次排序)
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {


                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("Sort",myList.get(i).get("Sort"));
                newMap.put("check", myList.get(i).get("Sort"));
                newMap.put("checkEnter","0");
                myList.set(i, newMap);
                Log.e("check2",myList.get(i).get("check"));
            } else {
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("Sort",myList.get(i).get("Sort"));
                newMap.put("check", "0");
                newMap.put("checkEnter","0");
                myList.set(i, newMap); // 替換

            }
        }
        //排序check (及撿完貨的排序)

        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("check"));
                String value2 = (o2.get("check"));
                return value1.compareTo(value2);

            }

        });

    }
    //在檢的時候 排序在最上面 (數量已滿的狀態)
    private void checkListArray3(String cProductIDeSQL) {
        for (int i = 0 ; i<myList.size() ; i++){
            newMap = new LinkedHashMap<String, String>();
            newMap.put("NowQty", myList.get(i).get("NowQty"));
            newMap.put("ProductNo", myList.get(i).get("ProductNo"));
            newMap.put("cProductName", myList.get(i).get("cProductName"));
            newMap.put("Qty", myList.get(i).get("Qty"));
            newMap.put("Sort",myList.get(i).get("Sort"));
            newMap.put("check", myList.get(i).get("check"));
            newMap.put("checkEnter","0");
            myList.set(i, newMap); // 替換

        }
        //判斷輸入的品號
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty")) ) {
                if(myList.get(i).get("ProductNo").equals(cProductIDeSQL)){
                    newMap = new LinkedHashMap<String, String>();
                    newMap.put("NowQty", myList.get(i).get("NowQty"));
                    newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                    newMap.put("cProductName", myList.get(i).get("cProductName"));
                    newMap.put("Qty", myList.get(i).get("Qty"));
                    newMap.put("Sort",myList.get(i).get("Sort"));
                    newMap.put("check", "0");
                    newMap.put("checkEnter","1");
                    myList.set(i, newMap); // 替換
                    Log.e("checkEnter",myList.get(i).get("checkEnter"));
                }else {
                    newMap = new LinkedHashMap<String, String>();
                    newMap.put("NowQty", myList.get(i).get("NowQty"));
                    newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                    newMap.put("cProductName", myList.get(i).get("cProductName"));
                    newMap.put("Qty", myList.get(i).get("Qty"));
                    newMap.put("Sort",myList.get(i).get("Sort"));
                    newMap.put("check", "0");
                    newMap.put("checkEnter","0");
                    myList.set(i, newMap); // 替換
                    Log.e("checkEnter2",myList.get(i).get("ProductNo"));
                }

            }else {
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("Sort",myList.get(i).get("Sort"));
                newMap.put("check", "0");
                newMap.put("checkEnter","0");
                myList.set(i, newMap); // 替換
                Log.e("checkEnter3",myList.get(i).get("checkEnter"));
            }
        }
        //先依照商品名稱排序
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("checkEnter"));
                String value2 = (o2.get("checkEnter"));
                return value2.compareTo(value1);

            }

        });

        //排序check (及撿完貨的排序)

        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("check"));
                String value2 = (o2.get("check"));
                return value1.compareTo(value2);

            }

        });
        // 排序完後 檢完貨的位置 假如檢完 勾勾留著
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                mCheckSet.add(i);
            } else {
                mCheckSet.remove(i);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CheckBox checkBox = (CheckBox) findViewById(checkBox5);
                if(mCheckSet.size()==myList.size()){
                    checkBox.setChecked(true);
                }else {
                    checkBox.setChecked(false);
                }
            }
        });

    }

    //在檢的時候 排序在最上面
    private void checkListArray4(String cProductIDeSQL) {

        //先依照商品名稱排序
        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("checkEnter"));
                String value2 = (o2.get("checkEnter"));
                return value2.compareTo(value1);

            }

        });


        for (int i = 0 ; i<myList.size() ; i++){
            newMap = new LinkedHashMap<String, String>();
            newMap.put("NowQty", myList.get(i).get("NowQty"));
            newMap.put("ProductNo", myList.get(i).get("ProductNo"));
            newMap.put("cProductName", myList.get(i).get("cProductName"));
            newMap.put("Qty", myList.get(i).get("Qty"));
            newMap.put("Sort",myList.get(i).get("Sort"));
            newMap.put("check", myList.get(i).get("check"));
            newMap.put("checkEnter","0");
            myList.set(i, newMap); // 替換

        }
        // 如果檢完貨 新添加的欄位(check)就等於sort(方便下一次排序)

        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                if(myList.get(i).get("ProductNo").equals(cProductIDeSQL)){
                    newMap = new LinkedHashMap<String, String>();
                    newMap.put("NowQty", myList.get(i).get("NowQty"));
                    newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                    newMap.put("cProductName", myList.get(i).get("cProductName"));
                    newMap.put("Qty", myList.get(i).get("Qty"));
                    newMap.put("Sort",myList.get(i).get("Sort"));
                    //newMap.put("check", myList.get(i).get("Sort"));
                    newMap.put("check", "0");
                    newMap.put("checkEnter","0");
                    myList.set(i, newMap);
                    Log.e("check2",myList.get(i).get("check"));
                }else {
                    newMap = new LinkedHashMap<String, String>();
                    newMap.put("NowQty", myList.get(i).get("NowQty"));
                    newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                    newMap.put("cProductName", myList.get(i).get("cProductName"));
                    newMap.put("Qty", myList.get(i).get("Qty"));
                    newMap.put("Sort",myList.get(i).get("Sort"));
                    newMap.put("check", myList.get(i).get("Sort"));
                    //newMap.put("check", "0");
                    newMap.put("checkEnter","0");
                    myList.set(i, newMap);
                    Log.e("check2",myList.get(i).get("check"));
                }


            } else {
                newMap = new LinkedHashMap<String, String>();
                newMap.put("NowQty", myList.get(i).get("NowQty"));
                newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                newMap.put("cProductName", myList.get(i).get("cProductName"));
                newMap.put("Qty", myList.get(i).get("Qty"));
                newMap.put("Sort",myList.get(i).get("Sort"));
                newMap.put("check", "0");
                newMap.put("checkEnter","0");
                myList.set(i, newMap); // 替換

            }
        }
        //排序check (及撿完貨的排序)

        Collections.sort(myList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

                String value1 = (o1.get("check"));
                String value2 = (o2.get("check"));
                return value1.compareTo(value2);

            }

        });
        // 排序完後 檢完貨的位置 假如檢完 勾勾留著
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) == Integer.parseInt(myList.get(i).get("Qty"))) {
                mCheckSet.add(i);
            } else {
                mCheckSet.remove(i);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CheckBox checkBox = (CheckBox) findViewById(checkBox5);
                if(mCheckSet.size()==myList.size()){
                    checkBox.setChecked(true);
                }else {
                    checkBox.setChecked(false);
                }
            }
        });
    }
    //checkbox 勾就全勾
    private void setCheckBox(){
        final CheckBox checkBox = (CheckBox) findViewById(checkBox5);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    for (int i = 0;i<myList.size();i++){
                        newMap = new LinkedHashMap<String, String>();
                        newMap.put("NowQty", myList.get(i).get("Qty"));
                        newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                        newMap.put("cProductName", myList.get(i).get("cProductName"));
                        newMap.put("Qty", myList.get(i).get("Qty"));
                        newMap.put("Sort",myList.get(i).get("Sort"));
                        newMap.put("check", myList.get(i).get("Sort"));
                        newMap.put("checkEnter",myList.get(i).get("0"));
                        myList.set(i, newMap);

                        //setNowQty(Integer.parseInt(myList.get(i).get("Qty")),myList.get(i).get("ProductNo"),false);
                        adapter.notifyDataSetChanged();
                        mCheckSet.add(i);
                        //若進入後 訂單數量和檢貨數量都已經檢滿 checkBox5 打勾勾
                    }


                }else {
                    Log.e("歸零","歸零");
                    for (int i = 0;i<myList.size();i++){

                        newMap = new LinkedHashMap<String, String>();
                        newMap.put("NowQty", "0");
                        newMap.put("ProductNo", myList.get(i).get("ProductNo"));
                        newMap.put("cProductName", myList.get(i).get("cProductName"));
                        newMap.put("Qty", myList.get(i).get("Qty"));
                        newMap.put("Sort",myList.get(i).get("Sort"));
                        newMap.put("check", "0");
                        newMap.put("checkEnter","0");
                        myList.set(i, newMap);

                        //setNowQty(-Integer.parseInt(myList.get(i).get("Qty")),myList.get(i).get("ProductNo"),false);
                        adapter.notifyDataSetChanged();
                        mCheckSet.remove(i);
                    }

                    if(mCheckSet.size()==myList.size()){
                        checkBox.setChecked(true);
                    }else {
                        checkBox.setChecked(false);
                    }
                }
                checkListArray();
            }
        });
    }

    //設定 Dialog
    private void setDialog() {
        myDialog = new ProgressDialog(ShipperOrderActivity.this);
        myDialog.setTitle("載入中");
        myDialog.setMessage("載入資訊中，請稍後！");
        myDialog.setCancelable(false);
        myDialog.show();
    }
    //動作按鍵
    public void onActivity(View v) {
        chooseActivity();
    }
    //動作按鍵的方法 (選擇檢貨或換人檢)
    private void chooseActivity() {
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        dialog_list.setTitle("動作");
        dialog_list.setItems(activity, new DialogInterface.OnClickListener() {
            @Override
            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Log.e("選取", activity[which]);
                Log.e("選取數字", String.valueOf(which));
                //換人檢
                if (which == 0) {
                    PostChangeInfo();
                    setDialog();
                }
                //結案
                else if (which == 1) {

                    if (checkGift == true){
                        if(checkUP()){
                            PostendInfo();
                            setDialog();
                        }else {
                            Toast.makeText(ShipperOrderActivity.this, "商品未檢完", Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        PostendInfo();
                        setDialog();
                    }

                }

            }
        });
        dialog_list.show();
    }
    //換人檢 用OkHttp PostAPI
    private void PostChangeInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"save\",\"UserID\":\"" + Application.UserID + "\",\"PickupNumbers\" :\"" + Application.check3 + "\",\"PickupProducts\":" + getPickupProducts() + "}";
        Log.e("換人檢POST", json);
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
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
                        Toast.makeText(ShipperOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //post 成功後執行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得回傳資料json 還是JSON檔
                myDialog.dismiss();
                String json = response.body().string();
                Log.e("換人檢POST的回傳值", json);
                Log.e("換人檢URL",url);
                changeEnd(json);
            }


        });
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
            upMap.put("\"NowQty\"", myList.get(i).get("NowQty"));
            upList.add((LinkedHashMap<String, String>) upMap);
        }
        Log.e("upList", String.valueOf(upList));
        String upString = String.valueOf(upList).replaceAll("=", ":");
        String upStringList = upString.replaceAll(", ", ",");
        Log.e("upStringList", String.valueOf(upStringList));
        return upStringList;
    }
    //POST成功後取得1跳回前一頁
    private void changeEnd(String json) {
        myDialog.dismiss();
        int result = 0;
        try {
            result = new JSONObject(json).getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 1) {
            Intent intent = new Intent(ShipperOrderActivity.this, ShipperCheck.class);
            startActivity(intent);
            ShipperOrderActivity.this.finish();
        }
    }

    private void PostendInfo() {

        final OkHttpClient client = new OkHttpClient();
        //要上傳的內容(JSON)--帳號登入
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"Token\":\"\" ,\"Action\":\"finish\",\"UserID\":\"" + Application.UserID + "\",\"PickupNumbers\" :\"" + Application.check3 + "\",\"PickupProducts\":" + getPickupProducts() + ",\"imgbase64\": " + Allbase64 + "}";
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
                myDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShipperOrderActivity.this, "請確認網路是否有連線", Toast.LENGTH_SHORT).show();
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
    //判斷是否有檢完
    private boolean checkUP() {
        int check = 0;
        for (int i = 0; i < myList.size(); i++) {
            if (Integer.parseInt((myList.get(i).get("NowQty"))) != Integer.parseInt(myList.get(i).get("Qty"))) {
                check++;
                Log.e("NOWQTY", myList.get(i).get("NowQty"));
                Log.e("QTY", myList.get(i).get("Qty"));

            }
        }
        if(check == 0){
            return true;
        }else {
            return false;
        }
    }

    //音效 短 (有找到)
    private void Bsound(){
        mSoundPool.play(mSoundID,1.0F,1.0F,0,0,0.0f);
    }
    //音效 長 (沒找到)
    private void Bsound1(){
        mSoundPool.play(mSoundID,1.0F,1.0F,0,0,1.0f);
    }
    //手機震動
    private void vibrator (){
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(1500);
    }

    //私用
    public File getExtermalStoragePrivateDir(String albumName) {
        File file = new File(getExternalFilesDir("條碼紀錄"), albumName);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created or exist");
        }
        return file;
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
    //輸入的條碼 有兩個以上商品
    //兩個都先判斷是否有在list裡 超過兩個在裡面才要跳提示 不然就直接判斷
    private void chooseThings() {
        if (check2ID(stringArray).equals("nothing")){
            Bsound1();
            Toast.makeText(ShipperOrderActivity.this, "查無商品", Toast.LENGTH_SHORT).show();
            focusEdit();
        }else if (check2ID(stringArray).equals("many")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("請選擇商品編號");
            builder.setItems(stringArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.e("點擊", stringArray[i]);
                    //判斷 所選擇的商品編號是否在listView裡
                    if(checkID(stringArray[i])==true){ //有
                        //判斷是+1還是NUM
                        if (checkAdd == false){
                            setNowQty(addInt,stringArray[i],true);
                            Log.e("setNowQty",stringArray[i]);
                        }else {
                            setAlertDialog(stringArray[i]);
                        }
                    }else { //沒有
                        Bsound1();
                        Toast.makeText(ShipperOrderActivity.this, "查無商品", Toast.LENGTH_SHORT).show();
                        focusEdit();
                    }
                }
            });

            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            setNowQty(addInt, check2ID(stringArray), true);
            Log.e("setNowQty","5");
            Bsound();
            focusEdit();
        }

    }
    private void setKeyboard(){
        final EditText editText = (EditText) findViewById(R.id.editText);
        //Android 對 EditText 取得 focus
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    //商品清單SQL
    private int setThingSQL(String barcode) {
        helper = new MyDBhelper(this, DB_NAME, null, 7);
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

    private void focusEdit(){
        final EditText editText = (EditText) findViewById(R.id.editText);
        EditText editText1 = (EditText)findViewById(R.id.editText7);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editText.requestFocus();
                return false;
            }
        });
        editText1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editText.requestFocus();
                return false;
            }
        });

    }
    //判斷條碼內的商品是否有在list裡 (兩個以上的商品)
    //沒有在list裡就回傳nothing 有一個就回傳商品號碼 超過一個就回傳many
    private String check2ID(String[] cProductIDeSQL) {
        int checkNum = 0;
        String cProductIDeSQL2 = null;
        for (int i = 0 ; i< cProductIDeSQL.length ; i ++){
            for (int i2 = 0; i2 < myList.size(); i2++) {
                if (cProductIDeSQL[i].equals(myList.get(i2).get("ProductNo"))) {
                    cProductIDeSQL2=cProductIDeSQL[i];
                    checkNum++;
                }
            }

        }
        if(checkNum<1){
            Log.e("checkNum","nothing");
            return "nothing";
        }else if (checkNum == 1){
            Log.e("checkNum",cProductIDeSQL2);
            return cProductIDeSQL2;
        }else {
            Log.e("checkNum","many");
            return "many";
        }
    }
    // 數量判斷 超過回傳true
    private boolean checkfull (int addInt , String cProductIDeSQL){
        for (int i = 0; i < myList.size(); i++) {
            if (cProductIDeSQL.equals(myList.get(i).get("ProductNo"))) {
                // 取出
                int i2 = Integer.parseInt(myList.get(i).get("NowQty"));
                int i4 = Integer.parseInt(myList.get(i).get("Qty"));
                Log.e("test NowQty", String.valueOf(i2));
                Log.e("test Qty", String.valueOf(i4));
                Log.e("test addIntQty", String.valueOf(addInt));

                if (i2 + addInt > i4){
                    return true;
                }else {
                    return false;
                }

            }

        }
        return false;
    }
    // 判斷商品名稱是否有重複 因為一商品會有多條碼編號 如果有重複取一即可
    private void check2ProductNo(){
        boolean isDistinct = false;
        for (int i = 0 ; i<stringArray.length ; i++){

            for(int j=0;j<i;j++){
                if(stringArray[i] .equals(stringArray[j])){
                    isDistinct = true;
                    break;
                }
            }
            if(isDistinct == true){
                cProductIDeSQL = stringArray[i];
                Log.e("isDistinct", String.valueOf(isDistinct));
                Log.e("isDistinct2", stringArray[i]);
            }else {

            }
        }
        if(isDistinct == true){
            //判斷是否有在listView裡
            if (checkID(cProductIDeSQL) == true){ //有
                Bsound();
                //Toast.makeText(this, cProductIDeSQL, Toast.LENGTH_SHORT).show();
                focusEdit();
                //判斷是+1還是NUM
                if (checkAdd == false){
                    setNowQty(addInt,cProductIDeSQL,true);
                    Log.e("setNowQty","2");
                }else {
                    setAlertDialog(cProductIDeSQL);
                }
            }else { //沒有
                Toast.makeText(this, "查無商品", Toast.LENGTH_SHORT).show();
                Bsound1();
                focusEdit();
            }
        }else {
            chooseThings();
        }
    }
}


