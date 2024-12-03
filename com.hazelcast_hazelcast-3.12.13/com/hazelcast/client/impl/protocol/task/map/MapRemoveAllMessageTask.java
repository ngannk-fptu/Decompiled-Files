/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapRemoveAllCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.EntryRemovingProcessor;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import java.security.Permission;
import java.util.Collections;
import java.util.Map;

public class MapRemoveAllMessageTask
extends AbstractMapAllPartitionsMessageTask<MapRemoveAllCodec.RequestParameters> {
    private Predicate predicate;

    public MapRemoveAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        boolean invokedOnPartition;
        if (!(this.predicate instanceof PartitionPredicate)) {
            super.processMessage();
            return;
        }
        int partitionId = this.getPartitionId();
        boolean bl = invokedOnPartition = partitionId != -1;
        if (!invokedOnPartition) {
            PartitionPredicate partitionPredicate = (PartitionPredicate)this.predicate;
            MapService mapService = (MapService)this.getService(this.getServiceName());
            MapServiceContext mapServiceContext = mapService.getMapServiceContext();
            MapContainer mapContainer = mapServiceContext.getMapContainer(((MapRemoveAllCodec.RequestParameters)this.parameters).name);
            PartitioningStrategy partitioningStrategy = mapContainer.getPartitioningStrategy();
            Object partitionKey = this.serializationService.toData(partitionPredicate.getPartitionKey(), partitioningStrategy);
            partitionId = this.nodeEngine.getPartitionService().getPartitionId((Data)partitionKey);
        }
        OperationFactory operationFactory = this.createOperationFactory();
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        if (invokedOnPartition) {
            Operation operation;
            if (operationFactory instanceof PartitionAwareOperationFactory) {
                PartitionAwareOperationFactory partitionAwareOperationFactory = (PartitionAwareOperationFactory)operationFactory;
                partitionAwareOperationFactory = partitionAwareOperationFactory.createFactoryOnRunner(this.nodeEngine, new int[]{partitionId});
                operation = partitionAwareOperationFactory.createPartitionOperation(partitionId);
            } else {
                operation = operationFactory.createOperation();
            }
            final int thisPartitionId = partitionId;
            operation.setCallerUuid(this.endpoint.getUuid());
            InternalCompletableFuture future = operationService.invokeOnPartition(this.getServiceName(), operation, partitionId);
            future.andThen(new ExecutionCallback<Object>(){

                @Override
                public void onResponse(Object response) {
                    MapRemoveAllMessageTask.this.onResponse(Collections.singletonMap(thisPartitionId, response));
                }

                @Override
                public void onFailure(Throwable t) {
                    MapRemoveAllMessageTask.this.onFailure(t);
                }
            });
        } else {
            operationFactory = new OperationFactoryWrapper(operationFactory, this.endpoint.getUuid());
            ICompletableFuture future = operationService.invokeOnPartitionsAsync(this.getServiceName(), operationFactory, Collections.singletonList(partitionId));
            future.andThen(this);
        }
    }

    @Override
    protected OperationFactory createOperationFactory() {
        MapOperationProvider operationProvider = this.getOperationProvider(((MapRemoveAllCodec.RequestParameters)this.parameters).name);
        Predicate effectivePredicate = this.predicate instanceof PartitionPredicate ? ((PartitionPredicate)this.predicate).getTarget() : this.predicate;
        return operationProvider.createPartitionWideEntryWithPredicateOperationFactory(((MapRemoveAllCodec.RequestParameters)this.parameters).name, EntryRemovingProcessor.ENTRY_REMOVING_PROCESSOR, effectivePredicate);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        return null;
    }

    @Override
    protected MapRemoveAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        MapRemoveAllCodec.RequestParameters parameters = MapRemoveAllCodec.decodeRequest(clientMessage);
        this.predicate = (Predicate)this.serializationService.toObject(parameters.predicate);
        return parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapRemoveAllCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapRemoveAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapRemoveAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "removeAll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapRemoveAllCodec.RequestParameters)this.parameters).predicate};
    }
}

