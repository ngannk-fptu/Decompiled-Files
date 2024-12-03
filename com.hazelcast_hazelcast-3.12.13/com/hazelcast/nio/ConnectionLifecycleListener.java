/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.Connection;

public interface ConnectionLifecycleListener<T extends Connection> {
    public void onConnectionClose(T var1, Throwable var2, boolean var3);
}

