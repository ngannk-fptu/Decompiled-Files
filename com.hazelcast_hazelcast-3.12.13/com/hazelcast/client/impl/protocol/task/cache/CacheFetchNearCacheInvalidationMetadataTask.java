/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.operation.CacheGetInvalidationMetaDataOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheFetchNearCacheInvalidationMetadataCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;

public class CacheFetchNearCacheInvalidationMetadataTask
extends AbstractInvocationMessageTask<CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters> {
    public CacheFetchNearCacheInvalidationMetadataTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).address);
    }

    @Override
    protected Operation prepareOperation() {
        return new CacheGetInvalidationMetaDataOperation(((CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).names);
    }

    @Override
    protected CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = CacheFetchNearCacheInvalidationMetadataCodec.decodeRequest(clientMessage);
        ((CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).address);
        return (CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        CacheGetInvalidationMetaDataOperation.MetaDataResponse metaDataResponse = (CacheGetInvalidationMetaDataOperation.MetaDataResponse)response;
        return CacheFetchNearCacheInvalidationMetadataCodec.encodeResponse(metaDataResponse.getNamePartitionSequenceList().entrySet(), metaDataResponse.getPartitionUuidList().entrySet());
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).names};
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

