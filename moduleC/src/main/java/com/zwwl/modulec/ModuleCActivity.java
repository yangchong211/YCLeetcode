package com.zwwl.modulec;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yc.api.ApiManager;
import com.zwwl.moduleinterface.IAddressManager;
import com.zwwl.moduleinterface.IUpdateManager;

import java.util.concurrent.atomic.AtomicInteger;

public class ModuleCActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv1;
    private TextView mTv2;
    private IAddressManager iAddressManager;
    private AtomicInteger integer = new AtomicInteger(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);

        mTv1 = findViewById(R.id.tv_1);
        mTv2 = findViewById(R.id.tv_2);

        mTv1.setOnClickListener(this);

        iAddressManager = ApiManager.getInstance().getApi(IAddressManager.class);
    }

    @Override
    public void onClick(View v) {
        if (v == mTv1){
            iAddressManager.getAddressInfo(new IAddressManager.CallBack() {
                @SuppressLint("SetTextI18n")
                @Override
                public void select(String area, String detail) {
                    mTv2.setText("--area---"+area+"--detail--"+detail+"----"+integer.getAndIncrement());
                }
            });
        }
    }
}
