package com.yc.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ApiManager implements IRegister {


    /**
     * https://github.com/MoerFinance/ApiManager
     */

    private static final String TAG = "ApiManager";
    private volatile static ApiManager instance;
    private Map<Class, Class> apiImplementMap = new HashMap<>();

    private ApiManager() {
    }

    public static ApiManager getInstance() {
        if (instance == null) {
            synchronized (ApiManager.class) {
                if (instance == null) {
                    instance = new ApiManager();
                }
            }
        }
        return instance;
    }

    @Override
    public <I extends IApi, E extends I> void register(Class<I> apiInterface, Class<E> apiImplement) {
        apiImplementMap.put(apiInterface, apiImplement);
    }

    public void clear() {
        apiImplementMap.clear();
    }

    public <T extends IApi> T getApi(Class<T> tClass) {
        Class<? extends T> implementClass = apiImplementMap.get(tClass);
        if (implementClass == null) {
            try {
                String simpleName = tClass.getSimpleName();
                String name = ApiConstants.PACKAGE_NAME_CONTRACT + "." + simpleName +
                        ApiConstants.SEPARATOR + ApiConstants.CONTRACT;
                Class<?> aClass = Class.forName(name);
                Constructor<?> constructor = aClass.getConstructor();
                Object newInstance = constructor.newInstance();
                ((IApiContract) newInstance).register(this);
                implementClass = apiImplementMap.get(tClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (implementClass != null) {
            try {
                return implementClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ClassLoader classLoader = tClass.getClassLoader();
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                if (method.getName().equals("isPresent")) {
                    return false;
                }
                if (method.getReturnType() == void.class) {
                    return null;
                }
                throw new IllegalStateException("Empty object cannot invoke a method which is not" +
                        " returning void or its name is not isPresent. Please use isPresent() at first.");
            }
        };
        Object proxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{tClass}, invocationHandler);
        return (T) proxyInstance;
    }
}
