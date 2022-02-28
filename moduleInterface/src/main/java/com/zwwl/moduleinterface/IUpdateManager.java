package com.zwwl.moduleinterface;

import com.yc.api.route.IRoute;

public interface IUpdateManager extends IRoute {

    /**
     * 检测升级
     */
    void checkUpdate(UpdateManagerCallBack updateManagerCallBack);

    interface UpdateManagerCallBack {
        void updateCallBack(boolean isNeedUpdate);
    }

}
