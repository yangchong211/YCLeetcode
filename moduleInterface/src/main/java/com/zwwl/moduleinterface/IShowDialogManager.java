package com.zwwl.moduleinterface;

import com.yc.api.IRoute;

public interface IShowDialogManager extends IRoute {

    void showDialog(CallBack callBack);

    interface CallBack{
        void select(boolean sure);
    }

}
