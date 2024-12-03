/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public interface ConnectionListenable {
    public void addConnectionListener(ConnectionListener var1);
}

