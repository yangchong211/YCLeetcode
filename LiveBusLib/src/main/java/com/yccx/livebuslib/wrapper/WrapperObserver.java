package com.yccx.livebuslib.wrapper;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/12/23
 *     desc  : Observer<T>
 *     revise:
 * </pre>
 */
public class WrapperObserver<T> implements Observer<T> {

    private Observer<T> observer;

    public WrapperObserver(Observer<T> observer) {
        this.observer = observer;
    }

    /**
     * 调用方法是指刷新数据
     * @param t                         t数据
     */
    @Override
    public void onChanged(@Nullable T t) {
        if (isCallOnObserve()) {
            return;
        }
        try {
            observer.onChanged(t);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private boolean isCallOnObserve() {
        //返回一个表示堆栈转储的堆栈跟踪元素数组
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement element : stackTrace) {
                    if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                            "observeForever".equals(element.getMethodName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
