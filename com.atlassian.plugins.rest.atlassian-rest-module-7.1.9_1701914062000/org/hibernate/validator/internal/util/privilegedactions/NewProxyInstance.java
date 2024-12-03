/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.PrivilegedAction;

public final class NewProxyInstance<T>
implements PrivilegedAction<T> {
    private final ClassLoader classLoader;
    private final Class<?>[] interfaces;
    private final InvocationHandler invocationHandler;

    public static <T> NewProxyInstance<T> action(ClassLoader classLoader, Class<T> interfaze, InvocationHandler invocationHandler) {
        return new NewProxyInstance<T>(classLoader, interfaze, invocationHandler);
    }

    public static NewProxyInstance<Object> action(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
        return new NewProxyInstance<Object>(classLoader, interfaces, invocationHandler);
    }

    private NewProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
        this.classLoader = classLoader;
        this.interfaces = interfaces;
        this.invocationHandler = invocationHandler;
    }

    private NewProxyInstance(ClassLoader classLoader, Class<T> interfaze, InvocationHandler invocationHandler) {
        this.classLoader = classLoader;
        this.interfaces = new Class[]{interfaze};
        this.invocationHandler = invocationHandler;
    }

    @Override
    public T run() {
        return (T)Proxy.newProxyInstance(this.classLoader, this.interfaces, this.invocationHandler);
    }
}

