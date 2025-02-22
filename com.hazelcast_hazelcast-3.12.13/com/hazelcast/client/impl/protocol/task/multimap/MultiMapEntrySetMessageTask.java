/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapEntrySetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.EntrySetResponse;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiMapEntrySetMessageTask
extends AbstractAllPartitionsMessageTask<MultiMapEntrySetCodec.RequestParameters> {
    public MultiMapEntrySetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MultiMapOperationFactory(((MultiMapEntrySetCodec.RequestParameters)this.parameters).name, MultiMapOperationFactory.OperationFactoryType.ENTRY_SET);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        ArrayList<Map.Entry<Data, Data>> entries = new ArrayList<Map.Entry<Data, Data>>();
        for (Object obj : map.values()) {
            if (obj == null) continue;
            EntrySetResponse response = (EntrySetResponse)obj;
            Set<Map.Entry<Data, Data>> entrySet = response.getDataEntrySet();
            for (Map.Entry<Data, Data> entry : entrySet) {
                entries.add(entry);
            }
        }
        return entries;
    }

    @Override
    protected MultiMapEntrySetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapEntrySetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapEntrySetCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapEntrySetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapEntrySetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "entrySet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

