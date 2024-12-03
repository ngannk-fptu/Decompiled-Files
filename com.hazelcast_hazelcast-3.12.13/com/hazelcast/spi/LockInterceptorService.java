/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

public interface LockInterceptorService<T> {
    public void onBeforeLock(String var1, T var2);
}

