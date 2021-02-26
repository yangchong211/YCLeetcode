package com.yc.api;

public interface IRoute {

    default boolean isPresent() {
        return true;
    }
}
