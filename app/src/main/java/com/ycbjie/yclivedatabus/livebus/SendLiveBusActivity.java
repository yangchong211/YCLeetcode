package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.ycbjie.yclivedatabus.eventbus.EventBusUtils;
import com.ycbjie.yclivedatabus.eventbus.EventMessage;
import com.yccx.livebuslib.event.LiveDataBus;
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SendLiveBusActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_event);
        textView = findViewById(R.id.tv_1);
        findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveDataBus.get().with(Constant.LIVE_BUS1).postValue("逗比");
            }
        });
        LiveDataBus.get()
                .with(Constant.LIVE_BUS1, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        BusLogUtils.d("接收消息--SendLiveBusActivity------yc_bus----"+s);
                        textView.setText(s);
                    }
                });
    }

}
