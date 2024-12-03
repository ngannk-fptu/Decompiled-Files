/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapValuesCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MultiMapValuesMessageTask
extends AbstractAllPartitionsMessageTask<MultiMapValuesCodec.RequestParameters> {
    public MultiMapValuesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MultiMapOperationFactory(((MultiMapValuesCodec.RequestParameters)this.parameters).name, MultiMapOperationFactory.OperationFactoryType.VALUES);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        ArrayList list = new ArrayList();
        for (Object obj : map.values()) {
            MultiMapResponse response;
            Collection coll;
            if (obj == null || (coll = (response = (MultiMapResponse)obj).getCollection()) == null) continue;
            for (MultiMapRecord record : coll) {
                list.add(this.serializationService.toData(record.getObject()));
            }
        }
        return list;
    }

    @Override
    protected MultiMapValuesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapValuesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapValuesCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapValuesCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapValuesCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "values";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

