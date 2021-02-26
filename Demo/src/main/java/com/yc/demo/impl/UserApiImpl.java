package com.yc.demo.impl;

import android.content.Context;
import android.widget.Toast;

import com.yc.api.ApiImpl;
import com.zwwl.moduleinterface.IUserManager;


@ApiImpl(IUserManager.class)
public class UserApiImpl implements IUserManager {

    @Override
    public void login(Context context) {
        Toast.makeText(context, "逗比，去登陆", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getUserInfo() {
        return "获取用户信息";
    }



}
