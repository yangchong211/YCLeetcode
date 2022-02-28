package com.zwwl.moduleinterface;

import com.yc.api.route.IRoute;

public interface IAddressManager extends IRoute {

    void getAddressInfo(CallBack callBack);

    interface CallBack{
        void select(String area, String detail);
    }

}
