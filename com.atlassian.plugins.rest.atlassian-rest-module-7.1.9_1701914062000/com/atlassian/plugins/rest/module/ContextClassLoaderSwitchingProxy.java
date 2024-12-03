/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugins.rest.module.ChainingClassLoader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ContextClassLoaderSwitchingProxy
implements InvocationHandler {
    private final Object delegate;
    private final ClassLoader[] classLoaders;

    public ContextClassLoaderSwitchingProxy(Object delegate, ClassLoader ... classLoaders) {
        this.delegate = delegate;
        this.classLoaders = classLoaders;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        ChainingClassLoader chainingClassLoader = new ChainingClassLoader(this.classLoaders);
        try {
            Thread.currentThread().setContextClassLoader(chainingClassLoader);
            Object object = method.invoke(this.delegate, args);
            return object;
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
        finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}

