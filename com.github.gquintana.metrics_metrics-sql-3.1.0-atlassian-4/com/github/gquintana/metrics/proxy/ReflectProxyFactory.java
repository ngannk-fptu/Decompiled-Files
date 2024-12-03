/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.AbstractProxyFactory;
import com.github.gquintana.metrics.proxy.ProxyClass;
import com.github.gquintana.metrics.proxy.ProxyHandler;
import java.lang.reflect.Proxy;

public class ReflectProxyFactory
extends AbstractProxyFactory {
    @Override
    public <T> T newProxy(ProxyHandler<T> proxyHandler, ProxyClass proxyClass) {
        return (T)Proxy.newProxyInstance(proxyClass.getClassLoader(), proxyClass.getInterfaces(), proxyHandler);
    }
}

