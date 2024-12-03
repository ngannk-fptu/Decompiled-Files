/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.CachePartitionEventData;
import com.hazelcast.cache.impl.event.CachePartitionLostEvent;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public class InternalCachePartitionLostListenerAdapter
implements CacheEventListener {
    private final CachePartitionLostListener partitionLostListener;

    public InternalCachePartitionLostListenerAdapter(CachePartitionLostListener partitionLostListener) {
        this.partitionLostListener = partitionLostListener;
    }

    @Override
    public void handleEvent(Object eventObject) {
        CachePartitionEventData eventData = (CachePartitionEventData)eventObject;
        CachePartitionLostEvent event = new CachePartitionLostEvent(eventData.getName(), eventData.getMember(), CacheEventType.PARTITION_LOST.getType(), eventData.getPartitionId());
        this.partitionLostListener.partitionLost(event);
    }
}

