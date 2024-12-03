/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum TransactionalMultiMapMessageType {
    TRANSACTIONALMULTIMAP_PUT(4353),
    TRANSACTIONALMULTIMAP_GET(4354),
    TRANSACTIONALMULTIMAP_REMOVE(4355),
    TRANSACTIONALMULTIMAP_REMOVEENTRY(4356),
    TRANSACTIONALMULTIMAP_VALUECOUNT(4357),
    TRANSACTIONALMULTIMAP_SIZE(4358);

    private final int id;

    private TransactionalMultiMapMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

