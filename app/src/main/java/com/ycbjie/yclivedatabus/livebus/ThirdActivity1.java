package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.model.TextViewModel;

public class ThirdActivity1 extends AppCompatActivity {

    private TextView tvText;
    private TextViewModel model;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third1);
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
                model.getCurrentText().setValue(text);
            }
        });
        initLiveData();
    }

    private void initLiveData() {
        // 创建一个持有某种数据类型的LiveData (通常是在ViewModel中)
        model = ViewModelProviders.of(this).get(TextViewModel.class);
        // 创建一个定义了onChange()方法的观察者
        // 开始订阅
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newText) {
                // 更新数据
                tvText.setText(newText);
            }
        };
        // 通过 observe()方法连接观察者和LiveData，注意：observe()方法需要携带一个LifecycleOwner类
        model.getCurrentText().observe(this, nameObserver);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                model.getCurrentText().observe(ThirdActivity1.this, nameObserver);
            }
        }).start();*/
    }


    /*
    //注意，不要写成员类，否则直接导致崩溃。
    //报错日志：Cannot create an instance of class com.ycbjie.yclivedatabus.livebus.ThirdActivity$TextViewModel
    public class TextViewModel extends ViewModel {

        //LiveData是抽象类，MutableLiveData是具体实现类
        private MutableLiveData<String> mCurrentText;

        public MutableLiveData<String> getCurrentText() {
            if (mCurrentText == null) {
                mCurrentText = new MutableLiveData<>();
            }
            return mCurrentText;
        }

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
