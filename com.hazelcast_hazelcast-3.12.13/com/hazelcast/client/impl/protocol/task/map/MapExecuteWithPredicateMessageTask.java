/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapExecuteWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapExecuteWithPredicateMessageTask
extends AbstractCallableMessageTask<MapExecuteWithPredicateCodec.RequestParameters>
implements BlockingMessageTask {
    public MapExecuteWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        Predicate predicate = (Predicate)this.serializationService.toObject(((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).predicate);
        if (predicate instanceof PartitionPredicate) {
            return this.invokeOnPartition((PartitionPredicate)predicate, operationService);
        }
        OperationFactoryWrapper operationFactory = new OperationFactoryWrapper(this.createOperationFactory(predicate), this.endpoint.getUuid());
        Map<Integer, Object> map = operationService.invokeOnAllPartitions(this.getServiceName(), operationFactory);
        return this.reduce(map);
    }

    private Object invokeOnPartition(PartitionPredicate partitionPredicate, InternalOperationService operationService) {
        int partitionId = this.getPartitionId();
        Predicate predicate = partitionPredicate.getTarget();
        OperationFactory factory = this.createOperationFactory(predicate);
        InvocationBuilder invocationBuilder = operationService.createInvocationBuilder(this.getServiceName(), factory.createOperation(), partitionId);
        Object result = invocationBuilder.invoke().join();
        return this.reduce(Collections.singletonMap(partitionId, result));
    }

    private OperationFactory createOperationFactory(Predicate predicate) {
        MapOperationProvider operationProvider = this.getOperationProvider(((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).name);
        EntryProcessor entryProcessor = (EntryProcessor)this.serializationService.toObject(((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).entryProcessor);
        return operationProvider.createPartitionWideEntryWithPredicateOperationFactory(((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).name, entryProcessor, predicate);
    }

    private MapOperationProvider getOperationProvider(String mapName) {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        return mapServiceContext.getMapOperationProvider(mapName);
    }

    protected Object reduce(Map<Integer, Object> map) {
        ArrayList<Map.Entry<Data, Data>> dataMap = new ArrayList<Map.Entry<Data, Data>>();
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        for (Object o : map.values()) {
            if (o == null) continue;
            MapEntries mapEntries = (MapEntries)mapService.getMapServiceContext().toObject(o);
            mapEntries.putAllToList(dataMap);
        }
        return dataMap;
    }

    @Override
    protected MapExecuteWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapExecuteWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapExecuteWithPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).name, "put", "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "executeOnEntries";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).entryProcessor, ((MapExecuteWithPredicateCodec.RequestParameters)this.parameters).predicate};
    }
}

