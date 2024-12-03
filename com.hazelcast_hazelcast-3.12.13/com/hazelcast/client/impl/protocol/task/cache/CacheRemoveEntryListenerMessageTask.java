/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import java.security.Permission;

public class CacheRemoveEntryListenerMessageTask
extends AbstractRemoveListenerMessageTask<CacheRemoveEntryListenerCodec.RequestParameters> {
    public CacheRemoveEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected String getRegistrationId() {
        return ((CacheRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected boolean deRegisterListener() {
        CacheService service = (CacheService)this.getService("hz:impl:cacheService");
        return service.deregisterListener(((CacheRemoveEntryListenerCodec.RequestParameters)this.parameters).name, ((CacheRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected CacheRemoveEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheRemoveEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheRemoveEntryListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheRemoveEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheRemoveEntryListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "deregisterCacheEntryListener";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

