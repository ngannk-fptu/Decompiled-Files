/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapExecuteOnKeysCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMultiPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.SetUtil;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapExecuteOnKeysMessageTask
extends AbstractMultiPartitionMessageTask<MapExecuteOnKeysCodec.RequestParameters> {
    public MapExecuteOnKeysMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        EntryProcessor processor = (EntryProcessor)this.serializationService.toObject(((MapExecuteOnKeysCodec.RequestParameters)this.parameters).entryProcessor);
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapExecuteOnKeysCodec.RequestParameters)this.parameters).name);
        return operationProvider.createMultipleEntryOperationFactory(((MapExecuteOnKeysCodec.RequestParameters)this.parameters).name, new HashSet<Data>(((MapExecuteOnKeysCodec.RequestParameters)this.parameters).keys), processor);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        ArrayList<Map.Entry<Data, Data>> entries = new ArrayList<Map.Entry<Data, Data>>();
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        for (Object o : map.values()) {
            if (o == null) continue;
            MapEntries mapEntries = (MapEntries)mapService.getMapServiceContext().toObject(o);
            mapEntries.putAllToList(entries);
        }
        return entries;
    }

    @Override
    public Collection<Integer> getPartitions() {
        InternalPartitionService partitionService = this.nodeEngine.getPartitionService();
        int partitions = partitionService.getPartitionCount();
        int capacity = Math.min(partitions, ((MapExecuteOnKeysCodec.RequestParameters)this.parameters).keys.size());
        Set<Integer> partitionIds = SetUtil.createHashSet(capacity);
        Iterator<Data> iterator = ((MapExecuteOnKeysCodec.RequestParameters)this.parameters).keys.iterator();
        while (iterator.hasNext() && partitionIds.size() < partitions) {
            Data key = iterator.next();
            partitionIds.add(partitionService.getPartitionId(key));
        }
        return partitionIds;
    }

    @Override
    protected MapExecuteOnKeysCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapExecuteOnKeysCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapExecuteOnKeysCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapExecuteOnKeysCodec.RequestParameters)this.parameters).name, "put", "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapExecuteOnKeysCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "executeOnKeys";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapExecuteOnKeysCodec.RequestParameters)this.parameters).keys, ((MapExecuteOnKeysCodec.RequestParameters)this.parameters).entryProcessor};
    }
}

