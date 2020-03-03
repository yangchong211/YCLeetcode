package com.yccx.livebuslib.inter;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/12/23
 *     desc  : 自定义被观察者（BusObservable）
 *     revise: 定义成接口，方便后期维护和接口隔离
 * </pre>
 */
public interface BusObservable<T> {

    /*这些都是LiveData中的方法*/

    void setValue(T value);

    void postValue(T value);

    void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

    void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

    void observeForever(@NonNull Observer<T> observer);

    void observeStickyForever(@NonNull Observer<T> observer);

    void removeObserver(@NonNull Observer<T> observer);

    /*下面的这些为自定义的方法*/

    void postValueDelay(T value,long delay);

    @Deprecated
    void postValueInterval(T value,long interval,@NonNull String taskName);

    @Deprecated
    void stopPostInterval(@NonNull String taskName);

}
