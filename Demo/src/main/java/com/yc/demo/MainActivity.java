package com.yc.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zwwl.modulea.ModuleAActivity;
import com.zwwl.moduleb.ModuleBActivity;
import com.zwwl.modulec.ModuleCActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv3;
    private TextView mTv4;
    private TextView mTv5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mTv3 = findViewById(R.id.tv_3);
        mTv4 = findViewById(R.id.tv_4);
        mTv5 = findViewById(R.id.tv_5);


        mTv3.setOnClickListener(this);
        mTv4.setOnClickListener(this);
        mTv5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTv3){
            startActivity(new Intent(this, ModuleAActivity.class));
        } else if (v == mTv4){
            startActivity(new Intent(this, ModuleBActivity.class));
        } else if (v == mTv5){
            startActivity(new Intent(this, ModuleCActivity.class));
        }
    }
}
