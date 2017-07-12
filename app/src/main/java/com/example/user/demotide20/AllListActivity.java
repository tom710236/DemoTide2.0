package com.example.user.demotide20;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static com.example.user.demotide20.R.id.textView;
import static com.example.user.demotide20.R.layout.lview;


public class AllListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String cUserName, dateUp2,cUserID,dateUp=null;
    SQLiteDatabase db,db2;
    int i=0,i2=0;
    IconAdapter gAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);
        //開啟sql資料庫 用來抓取商品資訊筆數和更新次數
        cursor3();
        //用來顯示產品資訊撈取
        dateUp2="產品資訊撈取"+"("+i2+"筆資訊"+")"+"\n"+dateUp+"("+i+"次更新"+")";
        //取得上一頁的資訊
        getPreviousPage();
        //toolBar設定
        toolBar();
        //啟動Delay service 每次到這畫面就會啟動一次 所以先停止再啟動
        Intent intent2 = new Intent(this,Delay.class);
        stopService(intent2);
        startService(intent2);

        //自訂的ListView
        ListView list = (ListView) findViewById(R.id.list);
        gAdapter = new IconAdapter();
        list.setAdapter(gAdapter);
        list.setOnItemClickListener(this);
    }
    //啟動資料庫 查詢其內容
    private void cursor3(){
        MyDBhelper2 MyDB2 = new MyDBhelper2(this,"tblOrder2",null,1);
        db2=MyDB2.getWritableDatabase();

        //Cursor c=db2.rawQuery("SELECT * FROM "+"tblTable2", null);   //查詢全部欄位
        Cursor c = db2.query("tblTable2",                          // 資料表名字
                null,                                              // 要取出的欄位資料
                null,                                              // 查詢條件式(WHERE)
                null,                                              // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);                                             // Order By字串語法(排序)
        //往下一個 收尋
        while(c.moveToNext()) {
            dateUp = c.getString(c.getColumnIndex("cUpdateDT"));
            Log.e("email",dateUp);
        }
        i=c.getCount();
        //最後更新時間
        //Log.e("dateUp",dateUp);
        //更新次數
        Log.e("更新次數", String.valueOf(i));

        /***********************************************************
         * 另一個SQL
         */
        MyDBhelper MyDB = new MyDBhelper(this,"tblTable",null,1);
        db=MyDB.getWritableDatabase();
        Cursor c2 = db.query("tblTable",                          // 資料表名字
                null,                                              // 要取出的欄位資料
                null,                                              // 查詢條件式(WHERE)
                null,                                              // 查詢條件值字串陣列(若查詢條件式有問號 對應其問號的值)
                null,                                              // Group By字串語法
                null,                                              // Having字串法
                null);
        i2=c2.getCount();
        //產品資訊筆數
        Log.e("I2", String.valueOf(i2));

        dateUp2="產品資訊撈取"+"("+i2+"筆資訊"+")"+"\n"+dateUp+"("+i+"次更新"+")";
        Log.e("DATAUP2",dateUp2);

        //查看SQL的List (正式版沒有顯示出來)
        ListView lv = (ListView)findViewById(R.id.lv);
        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_expandable_list_item_2,
                //R.layout.lview2,
                c,
                new String[] {"_id","cUpdateDT"},
                //new String[] {"_id", "cProductID", "cProductName", "cGoodsNo", "cUpdateDT"},
                new int[] {android.R.id.text1,android.R.id.text2},
                //new int[] {R.id.textView19,R.id.textView18,R.id.textView17,R.id.textView16,R.id.textView15},
                0);
        lv.setAdapter(adapter);

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
                Intent intent = new Intent(AllListActivity.this, LoginActivity.class);
                startActivity(intent);
                AllListActivity.this.finish();
            }
        });
    }
    //取得上一頁傳來的資訊
    private void getPreviousPage() {
        Intent intent = getIntent();
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID",null);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }
    //自定的ListView的點擊方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //點擊後到另一頁 並把cUserName帶到另一頁
                Intent intent = new Intent(AllListActivity.this,ShipperActivity.class);
                Bundle bag = new Bundle();
                bag.putString("cUserName",cUserName);
                bag.putString("cUserID",cUserID);
                intent.putExtras(bag);
                startActivity(intent);
                AllListActivity.this.finish();
                break;
            case 1:
                Intent intent1 = new Intent(AllListActivity.this,PurchaseActivity.class);
                Bundle bag1 = new Bundle();
                bag1.putString("cUserName",cUserName);
                bag1.putString("cUserID",cUserID);
                intent1.putExtras(bag1);
                startActivity(intent1);
                AllListActivity.this.finish();
                break;
            case 2:
                Intent intent2 = new Intent(AllListActivity.this,BlackSingleActivity.class);
                Bundle bag2 = new Bundle();
                bag2.putString("cUserName",cUserName);
                bag2.putString("cUserID",cUserID);
                intent2.putExtras(bag2);
                startActivity(intent2);
                AllListActivity.this.finish();
                break;
            case 3:
                Intent intent3 = new Intent(AllListActivity.this,SeachBlackSingleActivity.class);
                Bundle bag3 = new Bundle();
                bag3.putString("cUserName",cUserName);
                bag3.putString("cUserID",cUserID);
                intent3.putExtras(bag3);
                startActivity(intent3);
                AllListActivity.this.finish();
                break;
            case 4:
                Intent intent4 = new Intent(AllListActivity.this,StorageActivity.class);
                Bundle bag4 = new Bundle();
                bag4.putString("cUserName",cUserName);
                bag4.putString("cUserID",cUserID);
                intent4.putExtras(bag4);
                startActivity(intent4);
                AllListActivity.this.finish();
                break;
            case 5:
                Intent intent5 = new Intent(AllListActivity.this,SystemActivity.class);
                Bundle bag5 = new Bundle();
                bag5.putString("cUserName",cUserName);
                bag5.putString("cUserID",cUserID);
                intent5.putExtras(bag5);
                startActivity(intent5);
                AllListActivity.this.finish();
                break;
            case 6:
                gAdapter.notifyDataSetChanged();
                break;
        }

    }


    //String[]{} 自訂 listView
    class IconAdapter extends BaseAdapter {
        String[] func = {"出貨單檢貨", "採購單檢貨", "空白表單","空白表單查詢","儲位與庫存管理","系統管理",dateUp2};

        //int陣列方式將功能儲存在icons陣列
        int[] icons = {R.drawable.ic_keyboard_arrow_right_black_24dp, R.drawable.ic_keyboard_arrow_right_black_24dp, R.drawable.ic_keyboard_arrow_right_black_24dp
                , R.drawable.ic_keyboard_arrow_right_black_24dp, R.drawable.ic_keyboard_arrow_right_black_24dp, R.drawable.ic_keyboard_arrow_right_black_24dp, R.drawable.ic_keyboard_arrow_right_black_24dp};

        @Override
        public int getCount() {
            return func.length;
        }

        @Override
        public Object getItem(int position) {
            return func[position];
        }

        @Override
        public long getItemId(int position) {
            return icons[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //設定listView
            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(lview, null);
                ImageView image = (ImageView) v.findViewById(R.id.img);
                TextView text = (TextView) v.findViewById(textView);
                //呼叫setImageResource方法設定圖示的圖檔資源
                image.setImageResource(icons[position]);
                //呼叫setText方法設定圖示上的文字
                text.setText(func[position]);
            }
            return v;

        }
    }
    //設定返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(AllListActivity.this)
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