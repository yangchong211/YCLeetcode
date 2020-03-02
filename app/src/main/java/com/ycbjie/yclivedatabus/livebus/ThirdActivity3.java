package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ycbjie.yclivedatabus.R;

public class ThirdActivity3 extends AppCompatActivity {

    private TextView tvText;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third2);
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
                LiveDataBus2.get().getChannel("yc_bus").setValue(text);
            }
        });
        initLiveData();
    }

    private void initLiveData() {
        LiveDataBus2.get().getChannel("yc_bus", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String newText) {
                        // 更新数据
                        tvText.setText(newText);
                    }
                });
    }


}
