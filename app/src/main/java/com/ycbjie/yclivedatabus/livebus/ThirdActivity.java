package com.ycbjie.yclivedatabus.livebus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ycbjie.yclivedatabus.R;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                startActivity(new Intent(this, ThirdActivity1.class));
                break;
            case R.id.tv_2:
                startActivity(new Intent(this, ThirdActivity2.class));
                break;
            case R.id.tv_3:
                startActivity(new Intent(this, ThirdActivity3.class));
                break;
            case R.id.tv_4:
                startActivity(new Intent(this, ThirdActivity4.class));
                break;
            default:
                break;
        }
    }
}
