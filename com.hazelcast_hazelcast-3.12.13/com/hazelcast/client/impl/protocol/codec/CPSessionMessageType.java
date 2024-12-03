/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPSessionMessageType {
    CPSESSION_CREATESESSION(8705),
    CPSESSION_CLOSESESSION(8706),
    CPSESSION_HEARTBEATSESSION(8707),
    CPSESSION_GENERATETHREADID(8708);

    private final int id;

    private CPSessionMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

