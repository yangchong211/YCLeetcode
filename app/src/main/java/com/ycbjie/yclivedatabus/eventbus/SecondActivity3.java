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
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecondActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        EventBusUtils.register(this);
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
                    BusLogUtils.d("接收消息--ThirdActivity------yc_bus---1-"+newText);
                    break;
                case Constant.EVENT_BUS2:
                    BusLogUtils.d("接收消息--ThirdActivity------yc_bus---2-"+newText);
                    break;
            }
        }
    }

}
