/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.multicast.impl;

import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@BinaryInterface
public class MulticastMemberInfo
implements Serializable {
    private String host;
    private int port;

    public MulticastMemberInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return this.host;
    }
}

