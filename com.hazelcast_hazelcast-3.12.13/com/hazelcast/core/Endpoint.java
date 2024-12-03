/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import java.net.SocketAddress;

public interface Endpoint {
    public String getUuid();

    public SocketAddress getSocketAddress();
}

