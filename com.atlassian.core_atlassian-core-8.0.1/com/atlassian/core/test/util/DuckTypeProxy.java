/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.test.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DuckTypeProxy {
    public static UnimplementedMethodHandler RETURN_NULL = new UnimplementedMethodHandler(){

        @Override
        public Object methodNotImplemented(Method method, Object[] args) {
            return null;
        }
    };
    public static UnimplementedMethodHandler THROW = new UnimplementedMethodHandler(){

        @Override
        public Object methodNotImplemented(Method method, Object[] args) {
            throw new UnsupportedOperationException(method.toString());
        }
    };

    public static Object getProxy(Class implementingClass, List delegates) {
        return DuckTypeProxy.getProxy(new Class[]{implementingClass}, delegates);
    }

    public static Object getProxy(Class implementingClass, List delegates, UnimplementedMethodHandler unimplementedMethodHandler) {
        return DuckTypeProxy.getProxy(new Class[]{implementingClass}, delegates, unimplementedMethodHandler);
    }

    public static Object getProxy(Class[] implementingClasses, List delegates) {
        return DuckTypeProxy.getProxy(implementingClasses, delegates, THROW);
    }

    public static Object getProxy(Class[] implementingClasses, List delegates, UnimplementedMethodHandler unimplementedMethodHandler) {
        return Proxy.newProxyInstance(DuckTypeProxy.class.getClassLoader(), implementingClasses, (InvocationHandler)new DuckTypeInvocationHandler(delegates, unimplementedMethodHandler));
    }

    public static Object getProxy(Class implementingClass, Object delegate) {
        return DuckTypeProxy.getProxy(new Class[]{implementingClass}, Arrays.asList(delegate));
    }

    private static class DuckTypeInvocationHandler
    implements InvocationHandler {
        private final List delegates;
        private final UnimplementedMethodHandler unimplementedMethodHandler;

        DuckTypeInvocationHandler(List handlers, UnimplementedMethodHandler unimplementedMethodHandler) {
            this.delegates = Collections.unmodifiableList(new ArrayList(handlers));
            this.unimplementedMethodHandler = unimplementedMethodHandler;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Object handler : this.delegates) {
                Method duckTypeMethod;
                try {
                    duckTypeMethod = handler.getClass().getMethod(method.getName(), method.getParameterTypes());
                }
                catch (NoSuchMethodException ignoreAndTryNext) {
                    continue;
                }
                try {
                    duckTypeMethod.setAccessible(true);
                    return duckTypeMethod.invoke(handler, args);
                }
                catch (IllegalArgumentException ignoreAndTryNext) {
                }
                catch (IllegalAccessException ignoreAndTryNext) {
                }
                catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
            return this.unimplementedMethodHandler.methodNotImplemented(method, args);
        }
    }

    public static interface UnimplementedMethodHandler {
        public Object methodNotImplemented(Method var1, Object[] var2);
    }
}

