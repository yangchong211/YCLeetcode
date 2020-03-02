package com.ycbjie.yclivedatabus.eventbus;


public class EventMessage<T> {

    private String flag;
    private T event;

    public EventMessage(String flag, T event) {
        this.flag = flag;
        this.event = event;
    }

    public String getFlag() {
        return flag;
    }

    public T getEvent() {
        return event;
    }


}