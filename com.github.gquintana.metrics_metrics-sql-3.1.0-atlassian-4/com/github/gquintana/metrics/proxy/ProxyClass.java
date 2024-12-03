/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.ProxyException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;

public final class ProxyClass {
    private final ClassLoader classLoader;
    private final Class<?>[] interfaces;
    private final int hashCode;

    public ProxyClass(ClassLoader classLoader, Class<?> ... interfaces) {
        this.classLoader = classLoader;
        this.interfaces = interfaces;
        Object[] hashValue = new Object[interfaces.length + 1];
        hashValue[0] = classLoader;
        System.arraycopy(interfaces, 0, hashValue, 1, interfaces.length);
        this.hashCode = Objects.hash(hashValue);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Class<?>[] getInterfaces() {
        return this.interfaces;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProxyClass that = (ProxyClass)o;
        return this.hashCode == that.hashCode && Objects.equals(this.classLoader, that.classLoader) && Arrays.equals(this.interfaces, that.interfaces);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public Class createClass() {
        return Proxy.getProxyClass(this.getClassLoader(), this.getInterfaces());
    }

    public Constructor createConstructor() {
        try {
            return this.createClass().getConstructor(InvocationHandler.class);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new ProxyException(noSuchMethodException);
        }
    }
}

