/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum TransactionalListMessageType {
    TRANSACTIONALLIST_ADD(4865),
    TRANSACTIONALLIST_REMOVE(4866),
    TRANSACTIONALLIST_SIZE(4867);

    private final int id;

    private TransactionalListMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

