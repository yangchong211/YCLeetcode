package com.ycbjie.yclivedatabus.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecondActivity1 extends AppCompatActivity {

    private TextView tvText;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second1);
        EventBusUtils.register(this);
        tvText = findViewById(R.id.tv_text);
        findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                String text;
                switch (count%5){
                    case 1:
                        text = "小杨真的是一个逗比么";
                        break;
                    case 2:
                        text = "逗比赶紧来star吧";
                        break;
                    case 3:
                        text = "小杨想成为大神";
                        break;
                    case 4:
                        text = "开始刷新数据啦";
                        break;
                    default:
                        text = "变化成默认的数据";
                        break;
                }
                EventMessage<String> message = new EventMessage<>(Constant.EVENT_BUS,text);
                EventBusUtils.post(message);

                //发送粘性消息
                EventMessage<String> message2 = new EventMessage<>(Constant.EVENT_BUS2,text);
                EventBusUtils.postSticky(message2);
            }
        });
        findViewById(R.id.tv_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity1.this, StickyEventActivity.class));
            }
        });
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
                    BusLogUtils.d("接收消息--SecondActivity1------yc_bus---1-"+newText);
                    break;
                case Constant.EVENT_BUS2:
                    BusLogUtils.d("接收消息--SecondActivity1------yc_bus---2-"+newText);
                    break;
                default:
                    break;
            }
        }
    }

}
