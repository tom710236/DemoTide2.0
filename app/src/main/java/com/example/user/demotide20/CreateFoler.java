package com.example.user.demotide20;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by TOM on 2017/11/22.
 */

public class CreateFoler extends AppCompatActivity {

    //判斷外部儲存空間是否可以讀寫
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("讀寫","可");
            return true;
        }
        return false;
    }
    //判斷外部空間是否可以儲存
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.e("儲存","可");
            return true;
        }
        return false;
    }

    //公用
    public File getExtermalStoragePublicDir(String albumName) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(file.mkdir()){
            File f = new File(file, albumName);
            if(f.mkdir()){
                return f;
            }
        }
        return new File(file, albumName);
    }
    //外部空間建立公開資料夾
    public void extelnalPublicCreateFoler(String logToday,String logBarcode,String logProductID,String logQty,String logNowQty,String logAdd){
        String fileName = "APK";
        File dir = getExtermalStoragePublicDir("aa");
        File f = new File(dir.getPath(), fileName);
        String data = "時間:"+logToday+",條碼:"+logBarcode+",商品名稱:"+logProductID+",訂單數量:"+logQty+",檢貨數量:"+logNowQty+",增加數量:"+logAdd+"\r\n";

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //私用
   public File getExtermalStoragePrivateDir(String albumName) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), albumName);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created or exist");
        }
        return file;
    }



    //外部空間建立私有資料夾
    public void extelnalPrivateCreateFoler(File dirFile ,String logBarcode, String logProductName, String logProductID, String logQty, String logNowQty, String logAdd){

        File dir = dirFile;
        String fileName = today2()+".txt";
        File f = new File(dir, fileName);

        String data = "時間:"+today()+",條碼:"+logBarcode+",商品名稱:"+logProductName+",商品編號:"+logProductID+",訂單數量:"+logQty+",檢貨數量:"+logNowQty+",增加數量:"+logAdd+"\r\n";

        try {
            //new FileOutputStream(f,true) 多加true 就可以複寫
            FileOutputStream outputStream = new FileOutputStream(f,true);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String today2(){
        String today2;
        Calendar mCal = Calendar.getInstance();
        String dateformat2=  "yyyyMMdd";
        SimpleDateFormat df2 = new SimpleDateFormat(dateformat2);
        today2 = df2.format(mCal.getTime());

        return today2;
    }

    public String today(){
        String today;
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy/MM/dd/ HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today = df.format(mCal.getTime());

        return today;
    }

}
