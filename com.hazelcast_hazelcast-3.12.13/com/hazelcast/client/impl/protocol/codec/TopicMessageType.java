/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum TopicMessageType {
    TOPIC_PUBLISH(1025),
    TOPIC_ADDMESSAGELISTENER(1026),
    TOPIC_REMOVEMESSAGELISTENER(1027);

    private final int id;

    private TopicMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

