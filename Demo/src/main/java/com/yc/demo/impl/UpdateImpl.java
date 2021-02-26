package com.yc.demo.impl;


import com.yc.api.RouteImpl;
import com.zwwl.moduleinterface.IUpdateManager;

import java.util.Random;


@RouteImpl(IUpdateManager.class)
public class UpdateImpl implements IUpdateManager {


    @Override
    public void checkUpdate(UpdateManagerCallBack updateManagerCallBack) {
        Random random = new Random(100);
        int i = random.nextInt();
        if (i % 2 == 0){
            updateManagerCallBack.updateCallBack(true);
        } else {
            updateManagerCallBack.updateCallBack(false);
        }
    }



}
