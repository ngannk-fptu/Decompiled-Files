/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SdkInternalApi
public class ReflectionMethodInvoker<T, R> {
    private final Class<T> clazz;
    private final String methodName;
    private final Class<R> returnType;
    private final Class<?>[] parameterTypes;
    private Method targetMethod;

    public ReflectionMethodInvoker(Class<T> clazz, Class<R> returnType, String methodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    public R invoke(T obj, Object ... args) throws NoSuchMethodException {
        Method targetMethod = this.getTargetMethod();
        try {
            Object rawResult = targetMethod.invoke(obj, args);
            return this.returnType.cast(rawResult);
        }
        catch (IllegalAccessException e) {
            throw new SdkClientException(e);
        }
        catch (InvocationTargetException e) {
            throw new SdkClientException(e);
        }
    }

    public void initialize() throws NoSuchMethodException {
        this.getTargetMethod();
    }

    public boolean isInitialized() {
        return this.targetMethod != null;
    }

    private Method getTargetMethod() throws NoSuchMethodException {
        if (this.targetMethod != null) {
            return this.targetMethod;
        }
        try {
            this.targetMethod = this.clazz.getMethod(this.methodName, this.parameterTypes);
            return this.targetMethod;
        }
        catch (NullPointerException e) {
            throw new SdkClientException(e);
        }
    }
}

