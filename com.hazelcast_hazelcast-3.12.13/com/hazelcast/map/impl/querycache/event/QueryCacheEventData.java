/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

@BinaryInterface
public interface QueryCacheEventData
extends Sequenced,
EventData {
    public Object getKey();

    public Object getValue();

    public Data getDataKey();

    public Data getDataNewValue();

    public Data getDataOldValue();

    public long getCreationTime();

    public void setSerializationService(SerializationService var1);
}

