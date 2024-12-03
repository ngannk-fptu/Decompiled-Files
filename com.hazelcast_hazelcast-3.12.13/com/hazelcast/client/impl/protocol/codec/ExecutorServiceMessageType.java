/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum ExecutorServiceMessageType {
    EXECUTORSERVICE_SHUTDOWN(2305),
    EXECUTORSERVICE_ISSHUTDOWN(2306),
    EXECUTORSERVICE_CANCELONPARTITION(2307),
    EXECUTORSERVICE_CANCELONADDRESS(2308),
    EXECUTORSERVICE_SUBMITTOPARTITION(2309),
    EXECUTORSERVICE_SUBMITTOADDRESS(2310);

    private final int id;

    private ExecutorServiceMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

