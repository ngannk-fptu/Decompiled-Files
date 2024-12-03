/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum TransactionalQueueMessageType {
    TRANSACTIONALQUEUE_OFFER(5121),
    TRANSACTIONALQUEUE_TAKE(5122),
    TRANSACTIONALQUEUE_POLL(5123),
    TRANSACTIONALQUEUE_PEEK(5124),
    TRANSACTIONALQUEUE_SIZE(5125);

    private final int id;

    private TransactionalQueueMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

