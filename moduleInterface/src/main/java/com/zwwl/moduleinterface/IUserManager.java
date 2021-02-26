package com.zwwl.moduleinterface;

import android.content.Context;

import com.yc.api.IApi;


public interface IUserManager extends IApi {

    void login(Context context);

    String getUserInfo();


}
