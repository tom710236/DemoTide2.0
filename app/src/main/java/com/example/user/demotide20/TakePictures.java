package com.example.user.demotide20;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_PICTURES;

public class TakePictures extends AppCompatActivity {
    String cUserName, cUserID,activity,checked,order;
    private static final int REQUEST_CONTACTS = 1;
    final String[] picture = {"照片一", "照片二", "照片三","照片四","照片五"};
    Uri imgUri;
    ArrayList newAllBase64;
    Bitmap Abmp,Bbmp,Cbmp,Dbmp,Ebmp ;
    byte[] AArray,BArray,CArray,DArray,EArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pictures);

        toolBar();
        getPreviousPage();
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
                Intent intent = new Intent(TakePictures.this, ShipperActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName", cUserName);
                bag.putString("cUserID", cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                TakePictures.this.finish();
            }
        });
    }

    //取得上一頁傳過來的資料(ShipperOrderActivity)
    private void getPreviousPage() {
        //上一頁傳過來的資料取得
        Intent intent = getIntent();
        //取得Bundle物件後 再一一取得資料
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID", null);
        activity = bag.getString("activity",null);
        checked = bag.getString("checked", null);
        order = bag.getString("order", null);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
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
    //拍照並儲存 然後Intent的識別碼為100
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
    //顯示照片
    void showImg(ImageView imv,int i) {
        int iw, ih, vw, vh;
        boolean needRotate;  //用來儲存是否需要旋轉

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中

        iw = option.outWidth;   //由 option 中讀出圖檔寬度
        ih = option.outHeight;  //由 option 中讀出圖檔高度
        vw = imv.getWidth();    //取得 ImageView 的寬度
        vh = imv.getHeight();   //取得 ImageView 的高度

        int scaleFactor;
        if(iw<ih) {    //如果圖片的寬度小於高度
            needRotate = false;       				//不需要旋轉
           scaleFactor = Math.min(iw/vw, ih/vh);   // 計算縮小比率
        }
        else {
            needRotate = true;       				//需要旋轉
            scaleFactor = Math.min(iw/vh, ih/vw);   // 將 ImageView 的寬、高互換來計算縮小比率
        }

        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = scaleFactor*2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap  bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
        if(needRotate) { //如果需要旋轉
            Matrix matrix = new Matrix();  //建立 Matrix 物件
            matrix.postRotate(90);         //設定旋轉角度
            bmp = Bitmap.createBitmap(bmp , //用原來的 Bitmap 產生一個新的 Bitmap
                    0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        imv.setImageBitmap(bmp);

        //轉成byte
        if(i==101){
            makeBase64101(bmp);
        }
        if(i==102){
            makeBase64102(bmp);
        }
        if (i==103){
            makeBase64103(bmp);
        }
        if(i==104){
            makeBase64104(bmp);
        }
        if(i==105){
            makeBase64105(bmp);
        }
        new AlertDialog.Builder(this)
                .setTitle("圖檔資訊")
                .setMessage("圖檔路徑:" + imgUri.getPath() +
                        "\n 原始尺寸:" + iw + "x" + ih +
                        "\n 載入尺寸:"+bmp.getWidth()+"x"+bmp.getHeight()+
                        "\n 顯示尺寸:" + vw + "x" + vh
                )
                .setNegativeButton("關閉", null)
                .show();

    }
    public void onChoose (View v){
        choosePicture();
    }

    private void choosePicture(){
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(TakePictures.this);
        dialog_list.setTitle("挑選照片");
        dialog_list.setItems(picture, new DialogInterface.OnClickListener() {
            @Override
            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(TakePictures.this, "你選的是" + picture[which], Toast.LENGTH_SHORT).show();
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
        //放入多張照片的路徑 (從這邊下手)

        Uri convertUri(Uri uri) {
        if(uri.toString().substring(0, 7).equals("content")) {  //如果是以 "content" 開頭
            String[] colName = { MediaStore.MediaColumns.DATA };    //宣告要查詢的欄位
            Log.e("colName", String.valueOf(colName));
            /*
            for(int i =1; i<colName.length;i++){
                Log.e("colName",  colName[i]);
            }
                */
            Cursor cursor = getContentResolver().query(uri, colName,  //以 imgUri 進行查詢
                    null, null, null);
            cursor.moveToFirst();      //移到查詢結果的第一筆記錄
            uri = Uri.parse("file://" + cursor.getString(0)); //將路徑轉為 Uri
            cursor.close();     //關閉查詢結果
        }
        return uri;   //傳回 Uri 物件
    }



    private void makeBase64101(Bitmap bmp){
        Abmp = bmp;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Abmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        AArray= out.toByteArray();

        /*
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        Abase64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("Abase64",Abase64);
          */
    }
    private void makeBase64102(Bitmap bmp){
        Bbmp = bmp;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Abmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        BArray= out.toByteArray();
    }
    private void makeBase64103(Bitmap bmp){
        Cbmp = bmp;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Abmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        CArray= out.toByteArray();
    }
    private void makeBase64104(Bitmap bmp){
        Dbmp = bmp;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Abmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        DArray= out.toByteArray();
    }
    private void makeBase64105(Bitmap bmp){
        Ebmp = bmp;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Abmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        EArray= out.toByteArray();
    }
    public void onUpdate(View v){

        if(activity.equals("Shipper")){
            Intent intent = new Intent(this,ShipperOrderActivity.class);

            Bundle bag = new Bundle();
            //bag.putString("BITMAP", String.valueOf(AImv.getDrawingCache())); //这里可以放一个bitmap
            //bag.putString("newbase64", String.valueOf(newAllBase64));
            bag.putByteArray("AArray",AArray);
            bag.putByteArray("BArray",BArray);
            bag.putByteArray("CArray",CArray);
            bag.putByteArray("DArray",DArray);
            bag.putByteArray("EArray",EArray);
            bag.putString("cUserName", cUserName);
            bag.putString("cUserID", cUserID);
            bag.putString("checked", String.valueOf(checked));
            bag.putString("order",order);
            intent.putExtras(bag);
            startActivity(intent);
            this.finish();
        }

    }

}

