/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.ProxyClass;
import com.github.gquintana.metrics.proxy.ProxyFactory;
import com.github.gquintana.metrics.proxy.ProxyHandler;

public abstract class AbstractProxyFactory
implements ProxyFactory {
    public <X> X newProxy(ProxyHandler<X> proxyHandler, ClassLoader classLoader, Class<?> ... interfaces) {
        return this.newProxy(proxyHandler, new ProxyClass(classLoader, interfaces));
    }

    public <X> X newProxy(ProxyHandler<X> proxyHandler, Class<?> ... interfaces) {
        return this.newProxy(proxyHandler, Thread.currentThread().getContextClassLoader(), interfaces);
    }

    public <X> X newProxy(ProxyHandler<?> proxyHandler, Class<X> interfaces) {
        return (X)this.newProxy(proxyHandler, new Class[]{interfaces});
    }
}

