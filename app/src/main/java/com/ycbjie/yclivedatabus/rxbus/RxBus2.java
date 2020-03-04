package com.ycbjie.yclivedatabus.rxbus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public final class RxBus2 {

    private final Subject<Object> bus;
    private final Map<Class<?>, Object> stickyEventMap = new ConcurrentHashMap<>();

    private RxBus2() {
        // toSerialized method made bus thread safe
        bus = PublishSubject.create().toSerialized();
    }

    public static RxBus2 getInstance() {
        return Holder.BUS;
    }

    private static class Holder {
        private static final RxBus2 BUS = new RxBus2();
    }

    public void post(Object obj) {
        bus.onNext(obj);
    }

    public void postSticky(Object event) {
        synchronized (stickyEventMap) {
            stickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }


    public <T> Observable<T> toObservable(Class<T> tClass) {
        return bus.ofType(tClass);
    }

    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (stickyEventMap) {
            Observable<T> observable = bus.ofType(eventType);
            final Object event = stickyEventMap.get(eventType);
            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                        subscriber.onNext(eventType.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }


    public <T> T clearStickyEvent(Class<T> eventType) {
        synchronized (stickyEventMap) {
            return eventType.cast(stickyEventMap.remove(eventType));
        }
    }

    public void clearAllStickyEvent() {
        synchronized (stickyEventMap) {
            stickyEventMap.clear();
        }
    }

}