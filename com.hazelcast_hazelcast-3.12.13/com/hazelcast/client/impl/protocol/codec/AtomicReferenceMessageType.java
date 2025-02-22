/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum AtomicReferenceMessageType {
    ATOMICREFERENCE_APPLY(2817),
    ATOMICREFERENCE_ALTER(2818),
    ATOMICREFERENCE_ALTERANDGET(2819),
    ATOMICREFERENCE_GETANDALTER(2820),
    ATOMICREFERENCE_CONTAINS(2821),
    ATOMICREFERENCE_COMPAREANDSET(2822),
    ATOMICREFERENCE_GET(2824),
    ATOMICREFERENCE_SET(2825),
    ATOMICREFERENCE_CLEAR(2826),
    ATOMICREFERENCE_GETANDSET(2827),
    ATOMICREFERENCE_SETANDGET(2828),
    ATOMICREFERENCE_ISNULL(2829);

    private final int id;

    private AtomicReferenceMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

