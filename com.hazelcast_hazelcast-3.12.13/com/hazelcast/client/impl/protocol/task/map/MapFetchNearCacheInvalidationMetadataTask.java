/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapFetchNearCacheInvalidationMetadataCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapGetInvalidationMetaDataOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;

public class MapFetchNearCacheInvalidationMetadataTask
extends AbstractInvocationMessageTask<MapFetchNearCacheInvalidationMetadataCodec.RequestParameters> {
    public MapFetchNearCacheInvalidationMetadataTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((MapFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).address);
    }

    @Override
    protected Operation prepareOperation() {
        return new MapGetInvalidationMetaDataOperation(((MapFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).names);
    }

    @Override
    protected MapFetchNearCacheInvalidationMetadataCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = MapFetchNearCacheInvalidationMetadataCodec.decodeRequest(clientMessage);
        ((MapFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((MapFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).address);
        return (MapFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        MapGetInvalidationMetaDataOperation.MetaDataResponse metaDataResponse = (MapGetInvalidationMetaDataOperation.MetaDataResponse)response;
        return MapFetchNearCacheInvalidationMetadataCodec.encodeResponse(metaDataResponse.getNamePartitionSequenceList().entrySet(), metaDataResponse.getPartitionUuidList().entrySet());
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapFetchNearCacheInvalidationMetadataCodec.RequestParameters)this.parameters).names};
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
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

