package com.yc.api;

public interface IApi {

    default boolean isPresent() {
        return true;
    }
}
