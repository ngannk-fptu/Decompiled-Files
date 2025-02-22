/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum MapReduceMessageType {
    MAPREDUCE_CANCEL(3841),
    MAPREDUCE_JOBPROCESSINFORMATION(3842),
    MAPREDUCE_FORMAP(3843),
    MAPREDUCE_FORLIST(3844),
    MAPREDUCE_FORSET(3845),
    MAPREDUCE_FORMULTIMAP(3846),
    MAPREDUCE_FORCUSTOM(3847);

    private final int id;

    private MapReduceMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

