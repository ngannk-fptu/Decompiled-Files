/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum TransactionMessageType {
    TRANSACTION_COMMIT(5889),
    TRANSACTION_CREATE(5890),
    TRANSACTION_ROLLBACK(5891);

    private final int id;

    private TransactionMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

