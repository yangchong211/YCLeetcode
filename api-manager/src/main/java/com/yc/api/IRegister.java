package com.yc.api;

public interface IRegister {
    <I extends IRoute, E extends I> void register(Class<I> apiInterface, Class<E> apiImplement);
}
