/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheRemovePartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class CacheRemovePartitionLostListenerMessageTask
extends AbstractRemoveListenerMessageTask<CacheRemovePartitionLostListenerCodec.RequestParameters> {
    public CacheRemovePartitionLostListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        ICacheService service = (ICacheService)this.getService("hz:impl:cacheService");
        return service.getNodeEngine().getEventService().deregisterListener("hz:impl:cacheService", ((CacheRemovePartitionLostListenerCodec.RequestParameters)this.parameters).name, ((CacheRemovePartitionLostListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((CacheRemovePartitionLostListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected CacheRemovePartitionLostListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheRemovePartitionLostListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheRemovePartitionLostListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheRemovePartitionLostListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "removeCachePartitionLostListener";
    }
}

