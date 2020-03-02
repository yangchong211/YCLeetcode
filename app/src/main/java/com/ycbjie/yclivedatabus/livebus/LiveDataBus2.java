package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class LiveDataBus2 {

    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBus2() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus2 DEFAULT_BUS = new LiveDataBus2();
    }

    public static LiveDataBus2 get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    public <T> MutableLiveData<T> getChannel(String key, Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusMutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }

    public MutableLiveData<Object> getChannel(String key) {
        return getChannel(key, Object.class);
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private Observer<T> observer;

        public ObserverWrapper(Observer<T> observer) {
            this.observer = observer;
        }

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

    private static class BusMutableLiveData<T> extends MutableLiveData<T> {

        private Map<Observer, Observer> observerMap = new HashMap<>();

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            super.observe(owner, observer);
            try {
                hook(observer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void observeForever(@NonNull Observer<T> observer) {
            if (!observerMap.containsKey(observer)) {
                observerMap.put(observer, new ObserverWrapper(observer));
            }
            super.observeForever(observerMap.get(observer));
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            Observer realObserver = null;
            if (observerMap.containsKey(observer)) {
                realObserver = observerMap.remove(observer);
            } else {
                realObserver = observer;
            }
            super.removeObserver(realObserver);
        }

        private void hook(@NonNull Observer<T> observer) {
            try {
                Class<LiveData> classLiveData = LiveData.class;
                Field fieldObservers = classLiveData.getDeclaredField("mObservers");
                fieldObservers.setAccessible(true);
                Object objectObservers = fieldObservers.get(this);
                Class<?> classObservers = objectObservers.getClass();
                Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
                methodGet.setAccessible(true);
                Object objectWrapperEntry = methodGet.invoke(objectObservers, observer);
                Object objectWrapper = null;
                if (objectWrapperEntry instanceof Map.Entry) {
                    objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
                }
                if (objectWrapper != null) {
                    Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
                    Field fieldLastVersion = null;
                    if (classObserverWrapper != null) {
                        fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
                        fieldLastVersion.setAccessible(true);
                        Field fieldVersion = classLiveData.getDeclaredField("mVersion");
                        fieldVersion.setAccessible(true);
                        Object objectVersion = fieldVersion.get(this);
                        fieldLastVersion.set(objectWrapper, objectVersion);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }


        }
    }
}