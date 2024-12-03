/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.ProxyClass;
import com.github.gquintana.metrics.proxy.ProxyHandler;

public interface ProxyFactory {
    public <T> T newProxy(ProxyHandler<T> var1, ProxyClass var2);
}

