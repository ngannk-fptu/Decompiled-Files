/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheIterateEntriesCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Collections;

public class CacheIterateEntriesMessageTask
extends AbstractCacheMessageTask<CacheIterateEntriesCodec.RequestParameters> {
    public CacheIterateEntriesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheIterateEntriesCodec.RequestParameters)this.parameters).name);
        return operationProvider.createEntryIteratorOperation(((CacheIterateEntriesCodec.RequestParameters)this.parameters).tableIndex, ((CacheIterateEntriesCodec.RequestParameters)this.parameters).batch);
    }

    @Override
    protected CacheIterateEntriesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheIterateEntriesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        if (response == null) {
            return CacheIterateEntriesCodec.encodeResponse(0, Collections.emptyList());
        }
        CacheEntryIterationResult iteratorResult = (CacheEntryIterationResult)response;
        return CacheIterateEntriesCodec.encodeResponse(iteratorResult.getTableIndex(), iteratorResult.getEntries());
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheIterateEntriesCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheIterateEntriesCodec.RequestParameters)this.parameters).name;
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

