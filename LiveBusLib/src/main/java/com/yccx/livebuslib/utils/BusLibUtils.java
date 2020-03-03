package com.yccx.livebuslib.utils;


import android.os.Looper;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/07/18
 *     desc  : bus工具类
 *     revise:
 * </pre>
 */
public class BusLibUtils {

    /**
     * 判断是否是主线程
     * @return
     */
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

}
