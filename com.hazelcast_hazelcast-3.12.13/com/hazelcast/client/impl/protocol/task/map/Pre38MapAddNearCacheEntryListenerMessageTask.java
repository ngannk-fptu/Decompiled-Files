/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddNearCacheEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAddEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapClientNearCacheInvalidationListener;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.nearcache.invalidation.UuidFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import java.util.List;
import java.util.UUID;

public class Pre38MapAddNearCacheEntryListenerMessageTask
extends AbstractMapAddEntryListenerMessageTask<MapAddNearCacheEntryListenerCodec.RequestParameters> {
    public Pre38MapAddNearCacheEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MapAddNearCacheEntryListenerCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data keyData, Data newValueData, Data oldValueData, Data meringValueData, int type, String uuid, int numberOfAffectedEntries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddNearCacheEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    protected MapAddNearCacheEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddNearCacheEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddNearCacheEntryListenerCodec.encodeResponse((String)response);
    }

    @Override
    protected Object newMapListener() {
        String uuid = this.nodeEngine.getLocalMember().getUuid();
        long correlationId = this.clientMessage.getCorrelationId();
        return new Pre38NearCacheInvalidationListener(this.endpoint, uuid, correlationId);
    }

    @Override
    protected EventFilter getEventFilter() {
        return new EventListenerFilter(((MapAddNearCacheEntryListenerCodec.RequestParameters)this.parameters).listenerFlags, new UuidFilter(this.endpoint.getUuid()));
    }

    private final class Pre38NearCacheInvalidationListener
    extends AbstractMapClientNearCacheInvalidationListener {
        Pre38NearCacheInvalidationListener(ClientEndpoint endpoint, String localMemberUuid, long correlationId) {
            super(endpoint, localMemberUuid, correlationId);
        }

        @Override
        protected ClientMessage encodeBatchInvalidation(String name, List<Data> keys, List<String> sourceUuids, List<UUID> partitionUuids, List<Long> sequences) {
            return MapAddNearCacheEntryListenerCodec.encodeIMapBatchInvalidationEvent(keys, sourceUuids, partitionUuids, sequences);
        }

        @Override
        protected ClientMessage encodeSingleInvalidation(String name, Data key, String sourceUuid, UUID partitionUuid, long sequence) {
            return MapAddNearCacheEntryListenerCodec.encodeIMapInvalidationEvent(key, sourceUuid, partitionUuid, sequence);
        }

        @Override
        protected void sendMessageWithOrderKey(ClientMessage clientMessage, Object orderKey) {
            Pre38MapAddNearCacheEntryListenerMessageTask.this.sendClientMessage(orderKey, clientMessage);
        }

        @Override
        protected boolean canSendInvalidation(Invalidation invalidation) {
            return !Pre38MapAddNearCacheEntryListenerMessageTask.this.endpoint.getUuid().equals(invalidation.getSourceUuid());
        }
    }
}

