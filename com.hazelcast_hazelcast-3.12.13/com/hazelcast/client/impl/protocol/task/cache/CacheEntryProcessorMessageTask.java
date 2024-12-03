/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheEntryProcessorCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.ArrayList;
import javax.cache.processor.EntryProcessor;

public class CacheEntryProcessorMessageTask
extends AbstractCacheMessageTask<CacheEntryProcessorCodec.RequestParameters> {
    public CacheEntryProcessorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheService service = (CacheService)this.getService(this.getServiceName());
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheEntryProcessorCodec.RequestParameters)this.parameters).name);
        EntryProcessor entryProcessor = (EntryProcessor)service.toObject(((CacheEntryProcessorCodec.RequestParameters)this.parameters).entryProcessor);
        ArrayList<Object> argumentsList = new ArrayList<Object>(((CacheEntryProcessorCodec.RequestParameters)this.parameters).arguments.size());
        for (Data data : ((CacheEntryProcessorCodec.RequestParameters)this.parameters).arguments) {
            argumentsList.add(service.toObject(data));
        }
        return operationProvider.createEntryProcessorOperation(((CacheEntryProcessorCodec.RequestParameters)this.parameters).key, ((CacheEntryProcessorCodec.RequestParameters)this.parameters).completionId, entryProcessor, argumentsList.toArray());
    }

    @Override
    protected CacheEntryProcessorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheEntryProcessorCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheEntryProcessorCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheEntryProcessorCodec.RequestParameters)this.parameters).name, "read", "remove", "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheEntryProcessorCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheEntryProcessorCodec.RequestParameters)this.parameters).key, ((CacheEntryProcessorCodec.RequestParameters)this.parameters).entryProcessor, ((CacheEntryProcessorCodec.RequestParameters)this.parameters).arguments};
    }

    @Override
    public String getMethodName() {
        return "invoke";
    }
}

