/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheSetExpiryPolicyCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CacheSetExpiryPolicyMessageTask
extends AbstractCacheMessageTask<CacheSetExpiryPolicyCodec.RequestParameters> {
    public CacheSetExpiryPolicyMessageTask(ClientMessage message, Node node, Connection connection) {
        super(message, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return this.getOperationProvider(((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).name).createSetExpiryPolicyOperation(((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).keys, ((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).expiryPolicy);
    }

    @Override
    protected CacheSetExpiryPolicyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheSetExpiryPolicyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheSetExpiryPolicyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "setExpiryPolicy";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).keys, ((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).expiryPolicy};
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheSetExpiryPolicyCodec.RequestParameters)this.parameters).name, "remove");
    }
}

