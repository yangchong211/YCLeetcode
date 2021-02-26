package com.zwwl.moduleinterface;

import com.yc.api.IApi;

public interface IAddressManager extends IApi {

    void getAddressInfo(CallBack callBack);

    interface CallBack{
        void select(String area, String detail);
    }

}
