package com.zwwl.moduleinterface;

import com.yc.api.IApi;

public interface IShowDialogManager extends IApi {

    void showDialog(CallBack callBack);

    interface CallBack{
        void select(boolean sure);
    }

}
