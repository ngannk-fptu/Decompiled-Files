/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.proxy;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.apache.commons.pool2.UsageTracking;
import org.apache.commons.pool2.proxy.JdkProxyHandler;
import org.apache.commons.pool2.proxy.ProxySource;

public class JdkProxySource<T>
implements ProxySource<T> {
    private final ClassLoader classLoader;
    private final Class<?>[] interfaces;

    public JdkProxySource(ClassLoader classLoader, Class<?>[] interfaces) {
        this.classLoader = classLoader;
        this.interfaces = new Class[interfaces.length];
        System.arraycopy(interfaces, 0, this.interfaces, 0, interfaces.length);
    }

    @Override
    public T createProxy(T pooledObject, UsageTracking<T> usageTracking) {
        Object proxy = Proxy.newProxyInstance(this.classLoader, this.interfaces, new JdkProxyHandler<T>(pooledObject, usageTracking));
        return (T)proxy;
    }

    @Override
    public T resolveProxy(T proxy) {
        JdkProxyHandler jdkProxyHandler = (JdkProxyHandler)Proxy.getInvocationHandler(proxy);
        Object pooledObject = jdkProxyHandler.disableProxy();
        return pooledObject;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JdkProxySource [classLoader=");
        builder.append(this.classLoader);
        builder.append(", interfaces=");
        builder.append(Arrays.toString(this.interfaces));
        builder.append("]");
        return builder.toString();
    }
}

