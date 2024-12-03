/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveInvalidationListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class CacheRemoveInvalidationListenerMessageTask
extends AbstractRemoveListenerMessageTask<CacheRemoveInvalidationListenerCodec.RequestParameters> {
    public CacheRemoveInvalidationListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        CacheService service = (CacheService)this.getService("hz:impl:cacheService");
        return service.deregisterListener(((CacheRemoveInvalidationListenerCodec.RequestParameters)this.parameters).name, ((CacheRemoveInvalidationListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((CacheRemoveInvalidationListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected CacheRemoveInvalidationListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheRemoveInvalidationListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheRemoveInvalidationListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheRemoveInvalidationListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }
}

