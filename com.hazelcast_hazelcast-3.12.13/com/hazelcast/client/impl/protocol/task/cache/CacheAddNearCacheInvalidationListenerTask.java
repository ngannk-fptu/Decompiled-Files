/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheAddNearCacheInvalidationListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheClientNearCacheInvalidationListener;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import java.security.Permission;
import java.util.List;
import java.util.UUID;

public class CacheAddNearCacheInvalidationListenerTask
extends AbstractCallableMessageTask<CacheAddNearCacheInvalidationListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public CacheAddNearCacheInvalidationListenerTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        CacheService cacheService = (CacheService)this.getService("hz:impl:cacheService");
        CacheContext cacheContext = cacheService.getOrCreateCacheContext(((CacheAddNearCacheInvalidationListenerCodec.RequestParameters)this.parameters).name);
        NearCacheInvalidationListener listener = new NearCacheInvalidationListener(this.endpoint, cacheContext, this.nodeEngine.getLocalMember().getUuid(), this.clientMessage.getCorrelationId());
        String registrationId = cacheService.addInvalidationListener(((CacheAddNearCacheInvalidationListenerCodec.RequestParameters)this.parameters).name, listener, ((CacheAddNearCacheInvalidationListenerCodec.RequestParameters)this.parameters).localOnly);
        this.endpoint.addListenerDestroyAction("hz:impl:cacheService", ((CacheAddNearCacheInvalidationListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    @Override
    protected CacheAddNearCacheInvalidationListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheAddNearCacheInvalidationListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheAddNearCacheInvalidationListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheAddNearCacheInvalidationListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    private final class NearCacheInvalidationListener
    extends AbstractCacheClientNearCacheInvalidationListener {
        NearCacheInvalidationListener(ClientEndpoint endpoint, CacheContext cacheContext, String localMemberUuid, long correlationId) {
            super(endpoint, cacheContext, localMemberUuid, correlationId);
        }

        @Override
        protected ClientMessage encodeBatchInvalidation(String name, List<Data> keys, List<String> sourceUuids, List<UUID> partitionUuids, List<Long> sequences) {
            return CacheAddNearCacheInvalidationListenerCodec.encodeCacheBatchInvalidationEvent(name, keys, sourceUuids, partitionUuids, sequences);
        }

        @Override
        protected ClientMessage encodeSingleInvalidation(String name, Data key, String sourceUuid, UUID partitionUuid, long sequence) {
            return CacheAddNearCacheInvalidationListenerCodec.encodeCacheInvalidationEvent(name, key, sourceUuid, partitionUuid, sequence);
        }

        @Override
        protected void sendMessageWithOrderKey(ClientMessage clientMessage, Object orderKey) {
            CacheAddNearCacheInvalidationListenerTask.this.sendClientMessage(orderKey, clientMessage);
        }

        @Override
        protected boolean canSendInvalidation(Invalidation invalidation) {
            return true;
        }
    }
}

