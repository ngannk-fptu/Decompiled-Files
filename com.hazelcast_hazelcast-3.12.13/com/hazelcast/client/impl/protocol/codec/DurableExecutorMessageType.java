/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum DurableExecutorMessageType {
    DURABLEEXECUTOR_SHUTDOWN(6913),
    DURABLEEXECUTOR_ISSHUTDOWN(6914),
    DURABLEEXECUTOR_SUBMITTOPARTITION(6915),
    DURABLEEXECUTOR_RETRIEVERESULT(6916),
    DURABLEEXECUTOR_DISPOSERESULT(6917),
    DURABLEEXECUTOR_RETRIEVEANDDISPOSERESULT(6918);

    private final int id;

    private DurableExecutorMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

