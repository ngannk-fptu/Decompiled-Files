/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum PNCounterMessageType {
    PNCOUNTER_GET(8193),
    PNCOUNTER_ADD(8194),
    PNCOUNTER_GETCONFIGUREDREPLICACOUNT(8195);

    private final int id;

    private PNCounterMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

