/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.Connection;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public interface ConnectionListener {
    public void connectionAdded(Connection var1);

    public void connectionRemoved(Connection var1);
}

