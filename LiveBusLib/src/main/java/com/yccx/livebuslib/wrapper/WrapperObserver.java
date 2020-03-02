package com.yccx.livebuslib.wrapper;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

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
        if (observer != null) {
            if (isCallOnObserve()) {
                return;
            }
            observer.onChanged(t);
        }
    }

    private boolean isCallOnObserve() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            for (StackTraceElement element : stackTrace) {
                if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                        "observeForever".equals(element.getMethodName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
