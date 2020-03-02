package com.ycbjie.yclivedatabus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ycbjie.yclivedatabus.activity.FirstActivity;
import com.ycbjie.yclivedatabus.activity.SecondActivity;
import com.ycbjie.yclivedatabus.activity.ThirdActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
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
            default:
                break;
        }
    }
}
