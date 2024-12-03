/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheIterateCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.Collections;

public class CacheIterateMessageTask
extends AbstractCacheMessageTask<CacheIterateCodec.RequestParameters> {
    public CacheIterateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheIterateCodec.RequestParameters)this.parameters).name);
        return operationProvider.createKeyIteratorOperation(((CacheIterateCodec.RequestParameters)this.parameters).tableIndex, ((CacheIterateCodec.RequestParameters)this.parameters).batch);
    }

    @Override
    protected CacheIterateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheIterateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        if (response == null) {
            return CacheIterateCodec.encodeResponse(0, Collections.emptyList());
        }
        CacheKeyIterationResult keyIteratorResult = (CacheKeyIterationResult)response;
        return CacheIterateCodec.encodeResponse(keyIteratorResult.getTableIndex(), keyIteratorResult.getKeys());
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheIterateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }
}

