/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CountDownLatchMessageType {
    COUNTDOWNLATCH_AWAIT(3073),
    COUNTDOWNLATCH_COUNTDOWN(3074),
    COUNTDOWNLATCH_GETCOUNT(3075),
    COUNTDOWNLATCH_TRYSETCOUNT(3076);

    private final int id;

    private CountDownLatchMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

