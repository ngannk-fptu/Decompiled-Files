/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.operations.GetAllOperation;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;

public class MultiMapGetMessageTask
extends AbstractPartitionMessageTask<MultiMapGetCodec.RequestParameters> {
    public MultiMapGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        GetAllOperation operation = new GetAllOperation(((MultiMapGetCodec.RequestParameters)this.parameters).name, ((MultiMapGetCodec.RequestParameters)this.parameters).key);
        operation.setThreadId(((MultiMapGetCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    protected MultiMapGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ArrayList<Data> collection = new ArrayList<Data>();
        Collection responseCollection = ((MultiMapResponse)response).getCollection();
        if (responseCollection != null) {
            for (MultiMapRecord record : responseCollection) {
                collection.add((Data)this.serializationService.toData(record.getObject()));
            }
        }
        return MultiMapGetCodec.encodeResponse(collection);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapGetCodec.RequestParameters)this.parameters).key};
    }
}

