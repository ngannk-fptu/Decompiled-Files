/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPGroupMessageType {
    CPGROUP_CREATECPGROUP(8449),
    CPGROUP_DESTROYCPOBJECT(8450);

    private final int id;

    private CPGroupMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

