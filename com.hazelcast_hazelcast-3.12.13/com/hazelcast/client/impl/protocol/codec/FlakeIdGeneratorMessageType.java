/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum FlakeIdGeneratorMessageType {
    FLAKEIDGENERATOR_NEWIDBATCH(7937);

    private final int id;

    private FlakeIdGeneratorMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

