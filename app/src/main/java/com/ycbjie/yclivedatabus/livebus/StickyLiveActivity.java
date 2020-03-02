package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.event.LiveDataBus;
import com.yccx.livebuslib.utils.BusLogUtils;

public class StickyLiveActivity extends AppCompatActivity {

    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String newText) {
            BusLogUtils.d("接收消息--StickyEventActivity------yc_bus---4-"+newText);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky);
        //发送普通事件（非粘性事件）
        LiveDataBus2.get()
                .getChannel(Constant.YC_BUS, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        // TODO 可以发现这个收不到消息，这个显然是不行的
                        BusLogUtils.d("接收消息--StickyEventActivity------yc_bus--1--"+newText);
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



        //下面是发送粘性事件
        LiveDataBus.get()
                .with(Constant.YC_BUS2, String.class)
                .observeSticky(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        BusLogUtils.d("接收消息--StickyEventActivity------yc_bus---3-"+newText);
                    }
                });
        LiveDataBus.get()
                .with(Constant.YC_BUS2, String.class)
                .observeStickyForever(observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveDataBus.get()
                .with(Constant.YC_BUS2, String.class)
                .removeObserver(observer);
    }

}
