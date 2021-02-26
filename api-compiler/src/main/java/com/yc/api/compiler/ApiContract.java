package com.yc.api.compiler;

public class ApiContract<T> {

    private T api;
    private T apiImpl;

    public ApiContract(T api, T apiImpl) {
        this.api = api;
        this.apiImpl = apiImpl;
    }

    public T getApi() {
        return api;
    }

    public T getApiImpl() {
        return apiImpl;
    }
}
