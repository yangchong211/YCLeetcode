package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.event.LiveDataBus;
import com.yccx.livebuslib.utils.BusLogUtils;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);

        initBus();
    }

    private void initBus() {
        //测试可知，只有当页面可见时，才能接收到通知
        LiveDataBus2.get()
                .getChannel(Constant.YC_BUS, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        BusLogUtils.d("接收消息--ThirdActivity------yc_bus----"+newText);
                    }
                });
        LiveDataBus.get()
                .with(Constant.YC_BUS2, String.class)
                .observeSticky(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        BusLogUtils.d("接收消息--StickyEventActivity------yc_bus---2-"+newText);
                    }
                });
        LiveDataBus.get()
                .with(Constant.YC_BUS2, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        BusLogUtils.d("接收消息--StickyEventActivity------yc_bus---2-"+newText);
                    }
                });
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
