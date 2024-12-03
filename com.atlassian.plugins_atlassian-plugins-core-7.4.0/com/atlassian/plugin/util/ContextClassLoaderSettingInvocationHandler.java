/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.util.ClassLoaderStack;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ContextClassLoaderSettingInvocationHandler
implements InvocationHandler {
    private final Object service;
    private final ClassLoader serviceClassLoader;

    public ContextClassLoaderSettingInvocationHandler(Object service) {
        this.service = service;
        this.serviceClassLoader = service.getClass().getClassLoader();
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        ClassLoaderStack.push(this.serviceClassLoader);
        try {
            Object object = method.invoke(this.service, objects);
            return object;
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        finally {
            ClassLoaderStack.pop();
        }
    }
}

