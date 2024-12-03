/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.topic.impl.PublishOperation;
import com.hazelcast.topic.impl.TopicEvent;
import com.hazelcast.topic.impl.reliable.ReliableTopicMessage;

public final class TopicDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.topic", -18);
    public static final int PUBLISH = 0;
    public static final int TOPIC_EVENT = 1;
    public static final int RELIABLE_TOPIC_MESSAGE = 2;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 0: {
                        return new PublishOperation();
                    }
                    case 1: {
                        return new TopicEvent();
                    }
                    case 2: {
                        return new ReliableTopicMessage();
                    }
                }
                return null;
            }
        };
    }
}

