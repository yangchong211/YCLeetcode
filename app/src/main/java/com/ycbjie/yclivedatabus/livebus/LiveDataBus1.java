package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/9/11
 *     desc  : 最简单的liveDataBus代码案例，这里只是用作学习
 *     revise:
 * </pre>
 */
@Deprecated
public final class LiveDataBus1 {

    private final Map<String, MutableLiveData<Object>> bus;

    private LiveDataBus1() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus1 DATA_BUS = new LiveDataBus1();
    }

    public static LiveDataBus1 get() {
        return SingletonHolder.DATA_BUS;
    }

    public <T> MutableLiveData<T> getChannel(String target, Class<T> type) {
        if (!bus.containsKey(target)) {
            bus.put(target, new MutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(target);
    }

    public MutableLiveData<Object> getChannel(String target) {
        return getChannel(target, Object.class);
    }
}
