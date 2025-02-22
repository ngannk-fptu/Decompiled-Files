/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum AtomicLongMessageType {
    ATOMICLONG_APPLY(2561),
    ATOMICLONG_ALTER(2562),
    ATOMICLONG_ALTERANDGET(2563),
    ATOMICLONG_GETANDALTER(2564),
    ATOMICLONG_ADDANDGET(2565),
    ATOMICLONG_COMPAREANDSET(2566),
    ATOMICLONG_DECREMENTANDGET(2567),
    ATOMICLONG_GET(2568),
    ATOMICLONG_GETANDADD(2569),
    ATOMICLONG_GETANDSET(2570),
    ATOMICLONG_INCREMENTANDGET(2571),
    ATOMICLONG_GETANDINCREMENT(2572),
    ATOMICLONG_SET(2573);

    private final int id;

    private AtomicLongMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

