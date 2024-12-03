/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface ConstructionProxy<T> {
    public T newInstance(Object ... var1) throws InvocationTargetException;

    public InjectionPoint getInjectionPoint();

    public Constructor<T> getConstructor();

    public $ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors();
}

