/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum TransactionalSetMessageType {
    TRANSACTIONALSET_ADD(4609),
    TRANSACTIONALSET_REMOVE(4610),
    TRANSACTIONALSET_SIZE(4611);

    private final int id;

    private TransactionalSetMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

