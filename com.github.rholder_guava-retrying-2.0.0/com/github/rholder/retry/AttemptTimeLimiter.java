/*
 * Decompiled with CFR 0.152.
 */
package com.github.rholder.retry;

import java.util.concurrent.Callable;

public interface AttemptTimeLimiter<V> {
    public V call(Callable<V> var1) throws Exception;
}

