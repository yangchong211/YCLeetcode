package com.ycbjie.yclivedatabus.eventbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SendEventActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_event);
        EventBusUtils.register(this);
        textView = findViewById(R.id.tv_1);
        findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventMessage<String> message = new EventMessage<>(Constant.EVENT_BUS3,"逗比");
                EventBusUtils.post(message);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadSendEvent(EventMessage event) {
        if (event!=null && event.getFlag()!=null){
            String flag = event.getFlag();
            Object newText = event.getEvent();
            switch (flag){
                case Constant.EVENT_BUS3:
                    BusLogUtils.d("接收消息--SendLiveBusActivity------yc_bus---1-"+newText);
                    textView.setText((String) newText);
                    break;
                default:
                    break;
            }
        }
    }
}
