/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapExecuteOnAllKeysCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapExecuteOnAllKeysMessageTask
extends AbstractMapAllPartitionsMessageTask<MapExecuteOnAllKeysCodec.RequestParameters> {
    public MapExecuteOnAllKeysMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        MapOperationProvider operationProvider = this.getOperationProvider(((MapExecuteOnAllKeysCodec.RequestParameters)this.parameters).name);
        EntryProcessor entryProcessor = (EntryProcessor)this.serializationService.toObject(((MapExecuteOnAllKeysCodec.RequestParameters)this.parameters).entryProcessor);
        return operationProvider.createPartitionWideEntryOperationFactory(((MapExecuteOnAllKeysCodec.RequestParameters)this.parameters).name, entryProcessor);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        ArrayList<Map.Entry<Data, Data>> dataMap = new ArrayList<Map.Entry<Data, Data>>();
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        for (Object o : map.values()) {
            if (o == null) continue;
            MapEntries entries = (MapEntries)mapService.getMapServiceContext().toObject(o);
            entries.putAllToList(dataMap);
        }
        return dataMap;
    }

    @Override
    protected MapExecuteOnAllKeysCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapExecuteOnAllKeysCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapExecuteOnAllKeysCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapExecuteOnAllKeysCodec.RequestParameters)this.parameters).name, "put", "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapExecuteOnAllKeysCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "executeOnEntries";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapExecuteOnAllKeysCodec.RequestParameters)this.parameters).entryProcessor};
    }
}

