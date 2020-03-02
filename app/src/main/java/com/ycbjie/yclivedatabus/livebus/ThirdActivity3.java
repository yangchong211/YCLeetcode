package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.event.LiveDataBus;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ThirdActivity3 extends AppCompatActivity implements View.OnClickListener {

    private TextView tvText;
    private int count = 0;

    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            Toast.makeText(ThirdActivity3.this, s, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third3);
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
                LiveDataBus2.get().getChannel(Constant.YC_BUS).setValue(text);
                LiveDataBus.get().with(Constant.YC_BUS2).setValue(text);
            }
        });
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);
        findViewById(R.id.tv_5).setOnClickListener(this);
        findViewById(R.id.tv_6).setOnClickListener(this);
        findViewById(R.id.tv_7).setOnClickListener(this);
        initLiveData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_3:
                sendMsg();
                break;
            case R.id.tv_4:
                sendMsg1();
                break;
            case R.id.tv_5:
                sendMsgDelay5();
                break;
            case R.id.tv_6:
                closeAll();
                break;
            case R.id.tv_7:
                startActivity(new Intent(this, StickyLiveActivity.class));
                break;
            default:
                break;
        }
    }

    private void initLiveData() {
        LiveDataBus2.get()
                .getChannel(Constant.YC_BUS, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        tvText.setText(newText);
                    }
                });
        LiveDataBus2.get()
                .getChannel("random_data", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(ThirdActivity3.this,"随机消息"+  s, Toast.LENGTH_SHORT).show();
                    }
                });
        LiveDataBus2.get()
                .getChannel("random_data1", String.class)
                .observeForever(observer);
        LiveDataBus2.get()
                .getChannel("test", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        tvText.setText(s);
                        Toast.makeText(ThirdActivity3.this,"延迟消息"+ s, Toast.LENGTH_SHORT).show();
                    }
                });
        LiveDataBus2.get()
                .getChannel("close_all_page", Boolean.class)
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean b) {
                        if (b) {
                            finish();
                        }
                    }
                });
    }


    public void sendMsg() {
        Observable.just(new Random())
                .map(new Func1<Random, String>() {
                    @Override
                    public String call(Random random) {
                        return random.nextInt(100) + "";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LiveDataBus2.get().getChannel("random_data").setValue(s);
                    }
                });
    }

    public void sendMsg1() {
        Observable.just(new Random())
                .map(new Func1<Random, String>() {
                    @Override
                    public String call(Random random) {
                        return random.nextInt(100) + "";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LiveDataBus2.get().getChannel("random_data1").setValue(s);
                    }
                });
    }

    public void sendMsgDelay5() {
        Observable.just(new Random())
                .map(new Func1<Random, String>() {
                    @Override
                    public String call(Random random) {
                        return random.nextInt(100) + "";
                    }
                })
                .delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LiveDataBus2.get().getChannel("test").setValue(s);
                    }
                });
    }

    public void closeAll() {
        LiveDataBus2.get().getChannel("close_all_page").setValue(true);
    }

}
