/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.AbstractProxyFactory;
import com.github.gquintana.metrics.proxy.ProxyClass;
import com.github.gquintana.metrics.proxy.ProxyException;
import com.github.gquintana.metrics.proxy.ProxyHandler;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

public class CachingProxyFactory
extends AbstractProxyFactory {
    private final ConcurrentHashMap<ProxyClass, Constructor<?>> constructorCache = new ConcurrentHashMap();

    @Override
    public <T> T newProxy(ProxyHandler<T> proxyHandler, ProxyClass proxyClass) {
        Constructor constructor = this.constructorCache.get(proxyClass);
        if (constructor == null) {
            constructor = proxyClass.createConstructor();
            Constructor oldConstructor = this.constructorCache.putIfAbsent(proxyClass, constructor);
            constructor = oldConstructor == null ? constructor : oldConstructor;
        }
        try {
            return constructor.newInstance(proxyHandler);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new ProxyException(reflectiveOperationException);
        }
    }

    public void clearCache() {
        this.constructorCache.clear();
    }
}

