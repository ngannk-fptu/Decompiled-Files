/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheAddInvalidationListenerCodec;
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

public class Pre38CacheAddInvalidationListenerTask
extends AbstractCallableMessageTask<CacheAddInvalidationListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public Pre38CacheAddInvalidationListenerTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        CacheService cacheService = (CacheService)this.getService("hz:impl:cacheService");
        CacheContext cacheContext = cacheService.getOrCreateCacheContext(((CacheAddInvalidationListenerCodec.RequestParameters)this.parameters).name);
        String uuid = this.nodeEngine.getLocalMember().getUuid();
        long correlationId = this.clientMessage.getCorrelationId();
        Pre38NearCacheInvalidationListener listener = new Pre38NearCacheInvalidationListener(this.endpoint, cacheContext, uuid, correlationId);
        String registrationId = cacheService.addInvalidationListener(((CacheAddInvalidationListenerCodec.RequestParameters)this.parameters).name, listener, ((CacheAddInvalidationListenerCodec.RequestParameters)this.parameters).localOnly);
        this.endpoint.addListenerDestroyAction("hz:impl:cacheService", ((CacheAddInvalidationListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    @Override
    protected CacheAddInvalidationListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheAddInvalidationListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheAddInvalidationListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheAddInvalidationListenerCodec.RequestParameters)this.parameters).name;
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

    private final class Pre38NearCacheInvalidationListener
    extends AbstractCacheClientNearCacheInvalidationListener {
        Pre38NearCacheInvalidationListener(ClientEndpoint endpoint, CacheContext cacheContext, String localMemberUuid, long correlationId) {
            super(endpoint, cacheContext, localMemberUuid, correlationId);
        }

        @Override
        protected ClientMessage encodeBatchInvalidation(String name, List<Data> keys, List<String> sourceUuids, List<UUID> partitionUuids, List<Long> sequences) {
            return CacheAddInvalidationListenerCodec.encodeCacheBatchInvalidationEvent(name, keys, sourceUuids, partitionUuids, sequences);
        }

        @Override
        protected ClientMessage encodeSingleInvalidation(String name, Data key, String sourceUuid, UUID partitionUuid, long sequence) {
            return CacheAddInvalidationListenerCodec.encodeCacheInvalidationEvent(name, key, sourceUuid, partitionUuid, sequence);
        }

        @Override
        protected void sendMessageWithOrderKey(ClientMessage clientMessage, Object orderKey) {
            Pre38CacheAddInvalidationListenerTask.this.sendClientMessage(orderKey, clientMessage);
        }

        @Override
        protected boolean canSendInvalidation(Invalidation invalidation) {
            return !Pre38CacheAddInvalidationListenerTask.this.endpoint.getUuid().equals(invalidation.getSourceUuid());
        }
    }
}

