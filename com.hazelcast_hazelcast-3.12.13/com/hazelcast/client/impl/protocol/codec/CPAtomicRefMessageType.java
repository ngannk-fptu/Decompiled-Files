/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPAtomicRefMessageType {
    CPATOMICREF_APPLY(9217),
    CPATOMICREF_COMPAREANDSET(9218),
    CPATOMICREF_CONTAINS(9219),
    CPATOMICREF_GET(9221),
    CPATOMICREF_SET(9222);

    private final int id;

    private CPAtomicRefMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

