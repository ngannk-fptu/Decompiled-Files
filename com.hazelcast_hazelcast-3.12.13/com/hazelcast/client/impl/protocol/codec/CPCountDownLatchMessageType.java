/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPCountDownLatchMessageType {
    CPCOUNTDOWNLATCH_TRYSETCOUNT(9473),
    CPCOUNTDOWNLATCH_AWAIT(9474),
    CPCOUNTDOWNLATCH_COUNTDOWN(9475),
    CPCOUNTDOWNLATCH_GETCOUNT(9476),
    CPCOUNTDOWNLATCH_GETROUND(9477);

    private final int id;

    private CPCountDownLatchMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

