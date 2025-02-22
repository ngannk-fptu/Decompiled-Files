/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum ContinuousQueryMessageType {
    CONTINUOUSQUERY_PUBLISHERCREATEWITHVALUE(6145),
    CONTINUOUSQUERY_PUBLISHERCREATE(6146),
    CONTINUOUSQUERY_MADEPUBLISHABLE(6147),
    CONTINUOUSQUERY_ADDLISTENER(6148),
    CONTINUOUSQUERY_SETREADCURSOR(6149),
    CONTINUOUSQUERY_DESTROYCACHE(6150);

    private final int id;

    private ContinuousQueryMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

