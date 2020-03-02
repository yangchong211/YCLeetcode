package com.ycbjie.yclivedatabus.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.ycbjie.yclivedatabus.livebus.ThirdActivity1;
import com.ycbjie.yclivedatabus.livebus.ThirdActivity2;
import com.ycbjie.yclivedatabus.livebus.ThirdActivity3;
import com.ycbjie.yclivedatabus.livebus.ThirdActivity4;
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        EventBusUtils.register(this);
        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThread(EventMessage event) {
        if (event!=null && event.getFlag()!=null){
            String flag = event.getFlag();
            Object newText = event.getEvent();
            switch (flag){
                case Constant.EVENT_BUS:
                    BusLogUtils.d("接收消息--SecondActivity------yc_bus---1-"+newText);
                    break;
                case Constant.EVENT_BUS2:
                    BusLogUtils.d("接收消息--SecondActivity------yc_bus---2-"+newText);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                startActivity(new Intent(this, SecondActivity1.class));
                break;
            case R.id.tv_2:
                startActivity(new Intent(this, SecondActivity2.class));
                break;
            case R.id.tv_3:
                startActivity(new Intent(this, SecondActivity3.class));
                break;
            default:
                break;
        }
    }
}
