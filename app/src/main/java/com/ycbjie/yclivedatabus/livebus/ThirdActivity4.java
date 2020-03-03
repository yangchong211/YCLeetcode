package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.ycbjie.yclivedatabus.model.TextViewModel;
import com.yccx.livebuslib.event.LiveDataBus;
import com.yccx.livebuslib.utils.BusLogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThirdActivity4 extends AppCompatActivity {

    private TextView tvText;
    private int sendCount = 0;
    private int receiveCount = 0;
    private int sendCount2 = 0;
    private int receiveCount2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third4);
        tvText = findViewById(R.id.tv_text);
        findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               postValueCountTest();
            }
        });
        findViewById(R.id.tv_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postValueCountTest2();
            }
        });
        LiveDataBus2.get()
                .getChannel(Constant.LIVE_BUS3, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        receiveCount++;
                        BusLogUtils.d("接收消息--ThirdActivity4------yc_bus---1-"+s+"----"+receiveCount);
                    }
                });
        LiveDataBus.get()
                .with(Constant.LIVE_BUS4, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        receiveCount2++;
                        BusLogUtils.d("接收消息--ThirdActivity4------yc_bus---2-"+s+"----"+receiveCount2);
                    }
                });
    }


    public void postValueCountTest() {
        sendCount = 100;
        receiveCount = 0;
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < sendCount; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    LiveDataBus2.get().getChannel(Constant.LIVE_BUS3).postValue("test_1_data"+sendCount);
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BusLogUtils.d("sendCount: " + sendCount + " | receiveCount: " + receiveCount);
                Toast.makeText(ThirdActivity4.this, "sendCount: " + sendCount +
                        " | receiveCount: " + receiveCount, Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }


    public void postValueCountTest2() {
        sendCount2 = 100;
        receiveCount2 = 0;
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < sendCount2; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    LiveDataBus.get().with(Constant.LIVE_BUS4).postValue("test_2_data"+sendCount2);
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BusLogUtils.d("sendCount2: " + sendCount2 + " | receiveCount2: " + receiveCount2);
                Toast.makeText(ThirdActivity4.this, "sendCount2: " + sendCount2 +
                        " | receiveCount2: " + receiveCount2, Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
