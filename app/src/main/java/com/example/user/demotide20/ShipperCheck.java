package com.example.user.demotide20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ShipperCheck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_check);
    }

    public void onClick1 (View v){
        Intent intent = new Intent(ShipperCheck.this,ShipperActivity.class);
        startActivity(intent);
        ShipperCheck.this.finish();

    }

    public void onClick2 (View v){
        Intent intent = new Intent(ShipperCheck.this,Shipper2Activity.class);
        startActivity(intent);
        ShipperCheck.this.finish();
    }
}
