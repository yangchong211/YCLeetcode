package com.zwwl.moduleinterface;

import com.yc.api.IApi;

public interface IUpdateManager extends IApi {

    /**
     * 检测升级
     */
    void checkUpdate(UpdateManagerCallBack updateManagerCallBack);

    interface UpdateManagerCallBack {
        void updateCallBack(boolean isNeedUpdate);
    }

}
