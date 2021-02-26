package com.zwwl.modulea;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yc.api.ApiManager;
import com.zwwl.moduleinterface.IUserManager;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleAActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv1;
    private TextView mTv2;
    private IUserManager userApi;
    private AtomicInteger integer = new AtomicInteger(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);



        mTv1 = findViewById(R.id.tv_1);
        mTv2 = findViewById(R.id.tv_2);

        mTv1.setOnClickListener(this);
        mTv2.setOnClickListener(this);


        userApi = ApiManager.getInstance().getApi(IUserManager.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v == mTv1){
            String userInfo = userApi.getUserInfo();
            mTv1.setText(userInfo+"--"+integer.getAndIncrement());
        } else if (v == mTv2){
            userApi.login(this);
        }
    }
}
