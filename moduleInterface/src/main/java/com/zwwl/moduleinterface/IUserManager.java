package com.zwwl.moduleinterface;

import android.content.Context;

import com.yc.api.IRoute;


public interface IUserManager extends IRoute {

    void login(Context context);

    String getUserInfo();


}
