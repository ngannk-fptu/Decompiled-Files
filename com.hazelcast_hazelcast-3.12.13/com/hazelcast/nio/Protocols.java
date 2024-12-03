/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class Protocols {
    public static final int PROTOCOL_LENGTH = 3;
    public static final String CLUSTER = "HZC";
    public static final String CLIENT_BINARY_NEW = "CB2";
    public static final String REST = "HTTP";
    public static final String MEMCACHE = "Memcached";

    private Protocols() {
    }

    public static String toUserFriendlyString(String protocol) {
        if (CLUSTER.equals(protocol)) {
            return "Cluster Protocol";
        }
        if (CLIENT_BINARY_NEW.equals(protocol)) {
            return "Client Open Binary Protocol";
        }
        if (REST.equals(protocol)) {
            return "REST Protocol";
        }
        if (MEMCACHE.equals(protocol)) {
            return "MEMCACHE Protocol";
        }
        return "Unknown Protocol";
    }
}

