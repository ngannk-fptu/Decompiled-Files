/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPAtomicLongMessageType {
    CPATOMICLONG_APPLY(8961),
    CPATOMICLONG_ALTER(8962),
    CPATOMICLONG_ADDANDGET(8963),
    CPATOMICLONG_COMPAREANDSET(8964),
    CPATOMICLONG_GET(8965),
    CPATOMICLONG_GETANDADD(8966),
    CPATOMICLONG_GETANDSET(8967);

    private final int id;

    private CPAtomicLongMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

