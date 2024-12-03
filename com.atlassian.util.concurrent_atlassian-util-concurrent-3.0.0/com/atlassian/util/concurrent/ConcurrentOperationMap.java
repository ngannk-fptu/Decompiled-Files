/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface ConcurrentOperationMap<K, R> {
    public R runOperation(K var1, Callable<R> var2) throws ExecutionException;
}

