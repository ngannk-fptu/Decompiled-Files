/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListGetAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionGetAllOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;

public class ListGetAllMessageTask
extends AbstractPartitionMessageTask<ListGetAllCodec.RequestParameters> {
    public ListGetAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionGetAllOperation(((ListGetAllCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ListGetAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListGetAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListGetAllCodec.encodeResponse(((SerializableList)response).getCollection());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListGetAllCodec.RequestParameters)this.parameters).name};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListGetAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListGetAllCodec.RequestParameters)this.parameters).name;
    }
}

