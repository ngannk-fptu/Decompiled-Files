/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum XATransactionMessageType {
    XATRANSACTION_CLEARREMOTE(5633),
    XATRANSACTION_COLLECTTRANSACTIONS(5634),
    XATRANSACTION_FINALIZE(5635),
    XATRANSACTION_COMMIT(5636),
    XATRANSACTION_CREATE(5637),
    XATRANSACTION_PREPARE(5638),
    XATRANSACTION_ROLLBACK(5639);

    private final int id;

    private XATransactionMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

