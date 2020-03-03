package com.yccx.livebuslib.wrapper;

import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
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
public class SafeCastObserver<T> implements Observer<T> {

    @NonNull
    private final Observer<T> observer;

    SafeCastObserver(@NonNull Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public void onChanged(@Nullable T t) {
        try {
            //注意为了避免转换出现的异常，try-catch捕获
            observer.onChanged(t);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

}
