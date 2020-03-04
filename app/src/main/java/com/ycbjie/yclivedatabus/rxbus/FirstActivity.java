package com.ycbjie.yclivedatabus.rxbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ycbjie.yclivedatabus.R;
import com.yccx.livebuslib.utils.BusLogUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {


    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Disposable subscribe = RxBus2.getInstance()
                .toObservable(MessageEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageEvent>() {
                    @Override
                    public void accept(MessageEvent event) throws Exception {
                        BusLogUtils.d("--------RxBus2--------" + event.getData());
                        Toast.makeText(FirstActivity.this,event.getData(), Toast.LENGTH_SHORT).show();
                    }
                });
        disposable.add(subscribe);

        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //需要利用CompositeDisposable实现取消订阅
        disposable.dispose();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                MessageEvent event = new MessageEvent();
                event.setData("小杨真的是一个逗比么");
                RxBus2.getInstance().post(event);
                break;
            case R.id.tv_2:
                sendStickMessage();
                break;
            default:
                break;
        }
    }

    private void sendStickMessage() {
        //订阅消息事件
        Disposable subscribe = RxBus2.getInstance()
                .toObservableSticky(MessageEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageEvent>() {
                    @Override
                    public void accept(MessageEvent event) throws Exception {
                        BusLogUtils.d("--------RxBus2--------" + event.getData());
                        Toast.makeText(FirstActivity.this,event.getData(), Toast.LENGTH_SHORT).show();
                    }
                });
        disposable.add(subscribe);


        //发送消息
        MessageEvent event = new MessageEvent();
        event.setData("小杨逗比真的要努力成为android届的大神");
        RxBus2.getInstance().postSticky(event);
    }


}
