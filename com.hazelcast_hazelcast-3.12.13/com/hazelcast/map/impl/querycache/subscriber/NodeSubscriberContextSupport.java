/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContextSupport;
import com.hazelcast.map.impl.querycache.subscriber.operation.DestroyQueryCacheOperation;
import com.hazelcast.map.impl.querycache.subscriber.operation.SetReadCursorOperation;

public class NodeSubscriberContextSupport
implements SubscriberContextSupport {
    private final InternalSerializationService serializationService;

    public NodeSubscriberContextSupport(InternalSerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public Object createRecoveryOperation(String mapName, String cacheId, long sequence, int partitionId) {
        return new SetReadCursorOperation(mapName, cacheId, sequence, partitionId);
    }

    @Override
    public Boolean resolveResponseForRecoveryOperation(Object response) {
        return (Boolean)this.serializationService.toObject(response);
    }

    @Override
    public Object createDestroyQueryCacheOperation(String mapName, String cacheId) {
        return new DestroyQueryCacheOperation(mapName, cacheId);
    }
}

