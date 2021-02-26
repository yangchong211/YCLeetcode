package com.yc.demo.impl;


import com.yc.api.ApiImpl;
import com.zwwl.moduleinterface.IAddressManager;
import com.zwwl.moduleinterface.IUpdateManager;

import java.util.Random;


@ApiImpl(IAddressManager.class)
public class AddressImpl implements IAddressManager {

    @Override
    public void getAddressInfo(CallBack callBack) {
        Random random = new Random(100);
        int i = random.nextInt();
        if (i % 2 == 0){
            callBack.select("地址A","地址A的地址");
        } else {
            callBack.select("地址B","地址B的地址");
        }
    }


}
