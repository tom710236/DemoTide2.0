package com.example.user.demotide20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.user.demotide20.R.layout.lview;


public class AllListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String cUserName, dateUp2,cUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);
        getPreviousPage();
        toolBar();
        //啟動Delay service 每次到這畫面就會啟動一次 所以先停止再啟動
        Intent intent2 = new Intent(this,Delay.class);
        stopService(intent2);
        startService(intent2);

        //自訂ListView
        ListView list = (ListView) findViewById(R.id.list);
        IconAdapter gAdapter = new IconAdapter();
        list.setAdapter(gAdapter);
        list.setOnItemClickListener(this);
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

    private void getPreviousPage() {
        Intent intent = getIntent();
        Bundle bag = intent.getExtras();
        cUserName = bag.getString("cUserName", null);
        cUserID = bag.getString("cUserID",null);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(cUserName + "您好");
    }

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
        }

    }


    //String[]{} 自訂 listView
    class IconAdapter extends BaseAdapter {
        String[] func = {"出貨單檢貨", "採購單點貨", "空白表單","空白表單查詢","儲位與庫存管理","系統管理",dateUp2};

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
                TextView text = (TextView) v.findViewById(R.id.textView);
                //呼叫setImageResource方法設定圖示的圖檔資源
                image.setImageResource(icons[position]);
                //呼叫setText方法設定圖示上的文字
                text.setText(func[position]);
            }
            return v;

        }
    }
}