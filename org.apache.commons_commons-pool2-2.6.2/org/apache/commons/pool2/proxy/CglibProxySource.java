/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.cglib.proxy.Enhancer
 *  net.sf.cglib.proxy.Factory
 */
package org.apache.commons.pool2.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import org.apache.commons.pool2.UsageTracking;
import org.apache.commons.pool2.proxy.CglibProxyHandler;
import org.apache.commons.pool2.proxy.ProxySource;

public class CglibProxySource<T>
implements ProxySource<T> {
    private final Class<? extends T> superclass;

    public CglibProxySource(Class<? extends T> superclass) {
        this.superclass = superclass;
    }

    @Override
    public T createProxy(T pooledObject, UsageTracking<T> usageTracking) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.superclass);
        CglibProxyHandler<T> proxyInterceptor = new CglibProxyHandler<T>(pooledObject, usageTracking);
        enhancer.setCallback(proxyInterceptor);
        Object proxy = enhancer.create();
        return (T)proxy;
    }

    @Override
    public T resolveProxy(T proxy) {
        CglibProxyHandler cglibProxyHandler = (CglibProxyHandler)((Factory)proxy).getCallback(0);
        Object pooledObject = cglibProxyHandler.disableProxy();
        return pooledObject;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CglibProxySource [superclass=");
        builder.append(this.superclass);
        builder.append("]");
        return builder.toString();
    }
}

