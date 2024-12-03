/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public interface ExecutionCallback<V> {
    public void onResponse(V var1);

    public void onFailure(Throwable var1);
}

