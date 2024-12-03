/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BeanInterfaceProxy
implements InvocationHandler {
    private final Map<String, Object> beanState = new HashMap<String, Object>();

    private BeanInterfaceProxy() {
    }

    public static <T> T createProxy(Class<T> proxyInterface) {
        if (proxyInterface == null) {
            throw new NullPointerException("proxyInterface should not be null");
        }
        return proxyInterface.cast(Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[]{proxyInterface}, (InvocationHandler)new BeanInterfaceProxy()));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            if (method.getParameterTypes().length > 0) {
                throw new IllegalArgumentException(String.format("method %s.%s() should have no parameters to be a valid getter", method.getDeclaringClass().getName(), methodName));
            }
            return this.beanState.get(methodName.substring("get".length()));
        }
        if (methodName.startsWith("set")) {
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException(String.format("method  %s.%s() should have exactly one parameter to be a valid setter", method.getDeclaringClass().getName(), methodName));
            }
            this.beanState.put(methodName.substring("set".length()), args[0]);
            return proxy;
        }
        throw new IllegalArgumentException(String.format("method %s.%s() is not a valid getter/setter", method.getDeclaringClass().getName(), methodName));
    }
}

