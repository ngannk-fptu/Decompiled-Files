/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum ConditionMessageType {
    CONDITION_AWAIT(2049),
    CONDITION_BEFOREAWAIT(2050),
    CONDITION_SIGNAL(2051),
    CONDITION_SIGNALALL(2052);

    private final int id;

    private ConditionMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

