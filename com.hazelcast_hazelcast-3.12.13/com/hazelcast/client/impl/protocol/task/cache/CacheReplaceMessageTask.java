/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheReplaceCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.cache.expiry.ExpiryPolicy;

public class CacheReplaceMessageTask
extends AbstractCacheMessageTask<CacheReplaceCodec.RequestParameters> {
    public CacheReplaceMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheReplaceCodec.RequestParameters)this.parameters).name);
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)this.serializationService.toObject(((CacheReplaceCodec.RequestParameters)this.parameters).expiryPolicy);
        return operationProvider.createReplaceOperation(((CacheReplaceCodec.RequestParameters)this.parameters).key, ((CacheReplaceCodec.RequestParameters)this.parameters).oldValue, ((CacheReplaceCodec.RequestParameters)this.parameters).newValue, expiryPolicy, ((CacheReplaceCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CacheReplaceCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheReplaceCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheReplaceCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheReplaceCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheReplaceCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        if (((CacheReplaceCodec.RequestParameters)this.parameters).expiryPolicy == null && ((CacheReplaceCodec.RequestParameters)this.parameters).oldValue != null) {
            return new Object[]{((CacheReplaceCodec.RequestParameters)this.parameters).key, ((CacheReplaceCodec.RequestParameters)this.parameters).oldValue, ((CacheReplaceCodec.RequestParameters)this.parameters).newValue};
        }
        if (((CacheReplaceCodec.RequestParameters)this.parameters).oldValue == null && ((CacheReplaceCodec.RequestParameters)this.parameters).expiryPolicy == null) {
            return new Object[]{((CacheReplaceCodec.RequestParameters)this.parameters).key, ((CacheReplaceCodec.RequestParameters)this.parameters).newValue};
        }
        if (((CacheReplaceCodec.RequestParameters)this.parameters).oldValue == null && ((CacheReplaceCodec.RequestParameters)this.parameters).expiryPolicy != null) {
            return new Object[]{((CacheReplaceCodec.RequestParameters)this.parameters).key, ((CacheReplaceCodec.RequestParameters)this.parameters).newValue, ((CacheReplaceCodec.RequestParameters)this.parameters).expiryPolicy};
        }
        return new Object[]{((CacheReplaceCodec.RequestParameters)this.parameters).key, ((CacheReplaceCodec.RequestParameters)this.parameters).oldValue, ((CacheReplaceCodec.RequestParameters)this.parameters).newValue, ((CacheReplaceCodec.RequestParameters)this.parameters).expiryPolicy};
    }

    @Override
    public String getMethodName() {
        return "replace";
    }
}

