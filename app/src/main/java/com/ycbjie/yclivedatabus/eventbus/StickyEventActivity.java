package com.ycbjie.yclivedatabus.eventbus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.ycbjie.yclivedatabus.livebus.LiveDataBus2;
import com.yccx.livebuslib.event.LiveDataBus;
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StickyEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky);
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
                    BusLogUtils.d("接收消息--StickyEventActivity------yc_bus---1-"+newText);
                    break;
                case Constant.EVENT_BUS2:
                    BusLogUtils.d("接收消息--StickyEventActivity------yc_bus---2-"+newText);
                    break;
                default:
                    break;
            }
        }
    }

    @Subscribe(sticky = true ,threadMode = ThreadMode.MAIN)
    public void onMainThreadSticky(EventMessage event) {
        if (event!=null && event.getFlag()!=null){
            String flag = event.getFlag();
            Object newText = event.getEvent();
            switch (flag){
                case Constant.EVENT_BUS:
                    BusLogUtils.d("接收消息--StickyEventActivity---sticky---yc_bus---1-"+newText);
                    break;
                case Constant.EVENT_BUS2:
                    BusLogUtils.d("接收消息--StickyEventActivity---sticky---yc_bus---2-"+newText);
                    break;
                default:
                    break;
            }
        }
    }
}
