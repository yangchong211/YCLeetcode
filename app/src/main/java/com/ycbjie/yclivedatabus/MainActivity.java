package com.ycbjie.yclivedatabus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ycbjie.yclivedatabus.aac.AacActivity;
import com.ycbjie.yclivedatabus.aac.LifecycleActivity;
import com.ycbjie.yclivedatabus.rxbus.FirstActivity;
import com.ycbjie.yclivedatabus.eventbus.SecondActivity;
import com.ycbjie.yclivedatabus.livebus.ThirdActivity;
import com.yccx.livebuslib.utils.BusLogUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BusLogUtils.setIsLog(true);

        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                startActivity(new Intent(this, FirstActivity.class));
                break;
            case R.id.tv_2:
                startActivity(new Intent(this, SecondActivity.class));
                break;
            case R.id.tv_3:
                startActivity(new Intent(this, ThirdActivity.class));
                break;
            case R.id.tv_4:
                startActivity(new Intent(this, AacActivity.class));
                break;
            default:
                break;
        }
    }
}
