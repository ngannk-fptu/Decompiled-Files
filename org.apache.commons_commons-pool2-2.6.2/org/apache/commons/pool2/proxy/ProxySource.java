/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.proxy;

import org.apache.commons.pool2.UsageTracking;

interface ProxySource<T> {
    public T createProxy(T var1, UsageTracking<T> var2);

    public T resolveProxy(T var1);
}

