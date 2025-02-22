/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

public class DataAwareMessage
extends Message<Object> {
    private static final long serialVersionUID = 1L;
    private final transient Data messageData;
    private final transient SerializationService serializationService;

    public DataAwareMessage(String topicName, Data messageData, long publishTime, Member publishingMember, SerializationService serializationService) {
        super(topicName, null, publishTime, publishingMember);
        this.serializationService = serializationService;
        this.messageData = messageData;
    }

    @Override
    public Object getMessageObject() {
        if (this.messageObject == null && this.messageData != null) {
            this.messageObject = this.serializationService.toObject(this.messageData);
        }
        return this.messageObject;
    }

    public Data getMessageData() {
        return this.messageData;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new NotSerializableException();
    }
}

