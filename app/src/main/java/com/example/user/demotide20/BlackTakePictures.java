package com.example.user.demotide20;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_PICTURES;

public class BlackTakePictures extends AppCompatActivity {
    String cUserName, cUserID,activity,order;
    private static final int REQUEST_CONTACTS = 1;
    final String[] picture = {"照片一", "照片二", "照片三","照片四","照片五"};
    Uri imgUri,AImgUri,BImgUri,CImgUri,DImgUri,EImgUri;
    ArrayList AllImgUri;
    ImageView imv2;
    ArrayList<Map<String, String>> myList;
    int index ,index2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_take_pictures);
        toolBar();
        getPreviousPage();
        if(activity.equals("Black")){
            getShipperUri();
        }
    }
    private void toolBar() {
        //Toolbar 設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    }
    //取得上一頁傳過來的資料
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        myList = (ArrayList<Map<String, String>>) getIntent().getSerializableExtra("myList");
        Log.e("照片接收1myList", String.valueOf(myList));
        Bundle bag = intent.getExtras();
        AllImgUri = bag.getStringArrayList("AllImgUri");
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        activity = bag.getString("activity",null);
        index = bag.getInt("index",index);
        index2 = bag.getInt("index2",index2);
        order = bag.getString("order", null);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }
    private void getShipperUri(){
        if (AllImgUri != null && !AllImgUri.isEmpty()) {
            checkShipperUri();
        }
    }
    private void checkShipperUri(){
        if (AllImgUri.get(0) != null) {
            AImgUri = (Uri) AllImgUri.get(0);
            imv2 = (ImageView)findViewById(R.id.imageView14);
            showImg2(imv2,AImgUri);

        }
        if (AllImgUri.get(1) != null) {
            BImgUri = (Uri) AllImgUri.get(1);
            imv2 = (ImageView)findViewById(R.id.imageView15);
            showImg2(imv2,BImgUri);

        }
        if (AllImgUri.get(2) != null) {
            CImgUri = (Uri) AllImgUri.get(2);
            imv2 = (ImageView)findViewById(R.id.imageView16);
            showImg2(imv2,CImgUri);
        }
        if (AllImgUri.get(3) != null) {
            DImgUri = (Uri) AllImgUri.get(3);
            imv2 = (ImageView)findViewById(R.id.imageView17);
            showImg2(imv2,DImgUri);
        }
        if (AllImgUri.get(4) != null) {
            EImgUri = (Uri) AllImgUri.get(4);
            imv2 = (ImageView)findViewById(R.id.imageView18);
            showImg2(imv2,EImgUri);
        }
    }
    //回到訂單按鍵
    public void onNext(View v){
        if(activity.equals("Black")){
            //傳遞Uri
            AllImgUri = new ArrayList();
            AllImgUri.add(AImgUri);
            AllImgUri.add(BImgUri);
            AllImgUri.add(CImgUri);
            AllImgUri.add(DImgUri);
            AllImgUri.add(EImgUri);
            Log.e("ALLIMGURI", String.valueOf(AllImgUri));
            Intent intent = new Intent(BlackTakePictures.this,BlackSingleActivity.class);
            intent.putExtra("myList", myList);
            Log.e("照片傳遞myList", String.valueOf(myList));
            Bundle bag = new Bundle();
            bag.putStringArrayList("AllImgUri", AllImgUri);
            bag.putString("cUserName", cUserName);
            bag.putString("cUserID", cUserID);
            bag.putString("order",order);
            bag.putString("activity2","pictures");
            bag.putInt("index",index);
            bag.putInt("index2",index2);
            intent.putExtras(bag);
            startActivity(intent);
            this.finish();
        }else{
            Log.e("NO","NO");
        }

    }
    public void onTake (View v){
        takePicture();
    }
    //詢問 是否有拍照和讀取寫入的權限 沒有->詢問 有->執行拍照動作
    private void takePicture() {
        int permission = ActivityCompat.checkSelfPermission(this,
                CAMERA);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED||permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED ) {
            //若尚未取得權限，則向使用者要求允許聯絡人讀取與寫入的權限，REQUEST_CONTACTS常數未宣告則請按下Alt+Enter自動定義常數值。
            ActivityCompat.requestPermissions(this,
                    new String[]{CAMERA,WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_CONTACTS);
        } else {
            //已有權限，可進行以下方法
            makePicture();
        }
    }
    private void makePicture() {
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 100);
    }
    //依照Intent的識別碼來執行意圖
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {   //要求的意圖成功了
            if(requestCode==100){
                Intent it = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);//設為系統共享媒體檔
                sendBroadcast(it);
                //ImageView imv = (ImageView)findViewById(R.id.imageView19);
                //showImg(imv,requestCode);
            }
            else if(requestCode==101){
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                ImageView imv = (ImageView)findViewById(R.id.imageView14);
                showImg(imv,requestCode);
            }
            else if(requestCode==102) {
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                ImageView imv = (ImageView) findViewById(R.id.imageView15);
                showImg(imv,requestCode);
            }
            else if(requestCode==103) {
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                ImageView imv = (ImageView) findViewById(R.id.imageView16);
                showImg(imv,requestCode);
            }
            else if(requestCode==104) {
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                ImageView imv = (ImageView) findViewById(R.id.imageView17);
                showImg(imv,requestCode);
            }
            else if(requestCode==105) {
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                ImageView imv = (ImageView) findViewById(R.id.imageView18);
                showImg(imv,requestCode);
            }

        }
        else {
            Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
        }
    }
    //調整大小後顯示照片
    void showImg(ImageView imv,int i) {
        int iw, ih, vw, vh;

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中

        iw = option.outWidth;   //由 option 中讀出圖檔寬度
        ih = option.outHeight;  //由 option 中讀出圖檔高度
        vw = imv.getWidth();    //取得 ImageView 的寬度
        vh = imv.getHeight();   //取得 ImageView 的高度



        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔


        imv.setImageBitmap(bmp);

        //另存Uri
        if(i==101){
            saveImgUri101(imgUri);
        }
        if(i==102){
            saveImgUri102(imgUri);
        }
        if (i==103){
            saveImgUri103(imgUri);
        }
        if(i==104){
            saveImgUri104(imgUri);
        }
        if(i==105){
            saveImgUri105(imgUri);
        }
        /*
        new AlertDialog.Builder(this)
                .setTitle("圖檔資訊")
                .setMessage("圖檔路徑:" + imgUri.getPath() +
                        "\n 原始尺寸:" + iw + "x" + ih +
                        "\n 載入尺寸:"+bmp.getWidth()+"x"+bmp.getHeight()+
                        "\n 顯示尺寸:" + vw + "x" + vh
                )
                .setNegativeButton("關閉", null)
                .show();
        */
    }
    //查詢Uri
    Uri convertUri(Uri uri) {
        if(uri.toString().substring(0, 7).equals("content")) {  //如果是以 "content" 開頭
            String[] colName = { MediaStore.MediaColumns.DATA };    //宣告要查詢的欄位
            Cursor cursor = getContentResolver().query(uri, colName,  //以 imgUri 進行查詢
                    null, null, null);
            cursor.moveToFirst();      //移到查詢結果的第一筆記錄
            uri = Uri.parse("file://" + cursor.getString(0)); //將路徑轉為 Uri
            cursor.close();     //關閉查詢結果
        }
        return uri;   //傳回 Uri 物件
    }
    //記住所挑選照片的Uri
    private void saveImgUri101(Uri imgUri){
        AImgUri = imgUri;
    }
    private void saveImgUri102(Uri imgUri){
        BImgUri = imgUri;
    }
    private void saveImgUri103(Uri imgUri){
        CImgUri = imgUri;
    }
    private void saveImgUri104(Uri imgUri){
        DImgUri = imgUri;
    }
    private void saveImgUri105(Uri imgUri){
        EImgUri = imgUri;
    }

    //挑選照片的按鍵
    public void onChoose (View v){
        choosePicture();
    }
    // 挑選照片的方法 並編intent識別碼
    private void choosePicture(){
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(BlackTakePictures.this);
        dialog_list.setTitle("挑選照片");
        dialog_list.setItems(picture, new DialogInterface.OnClickListener() {
            @Override
            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //Toast.makeText(BlackTakePictures.this, "你選的是" + picture[which], Toast.LENGTH_SHORT).show();
                Log.e("選取", picture[which]);
                Log.e("選取數字", String.valueOf(which));
                if (which == 0) {
                    pickPicture1(101);
                }
                else if(which ==1){
                    pickPicture1(102);
                }
                else if(which ==2){
                    pickPicture1(103);
                }
                else if(which ==3){
                    pickPicture1(104);
                }
                else if(which ==4){
                    pickPicture1(105);
                }
            }
        });
        dialog_list.show();
    }
    //開啟相簿讀取
    private void pickPicture1(int i ) {
        int permission = ActivityCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            //若尚未取得權限，則向使用者要求允許聯絡人讀取與寫入的權限，REQUEST_CONTACTS常數未宣告則請按下Alt+Enter自動定義常數值。
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_CONTACTS);
        } else {
            //已有權限，可進行以下方法
            //i++;
            //makeSave();
            File picDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
            Uri uri2 = Uri.parse(String.valueOf(picDir));
            final Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            //開啟照片資料夾
            final Intent intent = new Intent(Intent.ACTION_PICK, uri);
            //複選
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(intent, i);
        }
    }

    void showImg2(ImageView imv2,Uri uri){

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(uri.getPath(), option);  //讀取圖檔資訊存入 Option 中


        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap  bmp = BitmapFactory.decodeFile(uri.getPath(), option); //載入圖檔


        imv2.setImageBitmap(bmp);

    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(BlackTakePictures.this)
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
