package com.yc.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/12/23
 *     desc  : 接口管理者
 *     revise:
 * </pre>
 */
public class TransferManager implements IRegister {

    private volatile static TransferManager instance;
    private Map<Class, Class> apiImplementMap = new HashMap<>();

    private TransferManager() {

    }

    public static TransferManager getInstance() {
        if (instance == null) {
            synchronized (TransferManager.class) {
                if (instance == null) {
                    instance = new TransferManager();
                }
            }
        }
        return instance;
    }

    @Override
    public <I extends IRoute, E extends I> void register(Class<I> apiInterface, Class<E> apiImplement) {
        apiImplementMap.put(apiInterface, apiImplement);
    }

    public void clear() {
        apiImplementMap.clear();
    }

    public <T extends IRoute> T getApi(Class<T> tClass) {
        Class<? extends T> implementClass = apiImplementMap.get(tClass);
        if (implementClass == null) {
            try {
                String simpleName = tClass.getSimpleName();
                String name = RouteConstants.PACKAGE_NAME_CONTRACT + "." + simpleName +
                        RouteConstants.SEPARATOR + RouteConstants.CONTRACT;
                Class<?> aClass = Class.forName(name);
                Constructor<?> constructor = aClass.getConstructor();
                Object newInstance = constructor.newInstance();
                ((IRouteContract) newInstance).register(this);
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
                throw new IllegalStateException("空对象不能调用非空对象的方法" +
                        " 返回void或它的名字不是isPresent。请先用isPresent()");
            }
        };
        Object proxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{tClass}, invocationHandler);
        return (T) proxyInstance;
    }
}
