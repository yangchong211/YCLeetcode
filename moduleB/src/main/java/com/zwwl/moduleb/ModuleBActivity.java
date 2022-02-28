package com.zwwl.moduleb;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yc.api.route.TransferManager;
import com.zwwl.moduleinterface.IUpdateManager;

import java.util.concurrent.atomic.AtomicInteger;

public class ModuleBActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv1;
    private TextView mTv2;
    private IUpdateManager iUpdateManager;
    private AtomicInteger integer = new AtomicInteger(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);

        mTv1 = findViewById(R.id.tv_1);
        mTv2 = findViewById(R.id.tv_2);

        mTv1.setOnClickListener(this);

        iUpdateManager = TransferManager.getInstance().getApi(IUpdateManager.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v == mTv1){
            iUpdateManager.checkUpdate(new IUpdateManager.UpdateManagerCallBack() {
                @Override
                public void updateCallBack(boolean isNeedUpdate) {
                    if (isNeedUpdate){
                        mTv2.setText("更新成功"+integer.getAndIncrement());
                    } else {
                        mTv2.setText("更新失败"+integer.getAndIncrement());
                    }
                }
            });
        }
    }
}
