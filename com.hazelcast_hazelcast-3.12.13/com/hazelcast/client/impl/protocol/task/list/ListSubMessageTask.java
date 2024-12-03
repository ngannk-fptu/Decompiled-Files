/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListSubCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListSubOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;

public class ListSubMessageTask
extends AbstractPartitionMessageTask<ListSubCodec.RequestParameters> {
    public ListSubMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListSubOperation(((ListSubCodec.RequestParameters)this.parameters).name, ((ListSubCodec.RequestParameters)this.parameters).from, ((ListSubCodec.RequestParameters)this.parameters).to);
    }

    @Override
    protected ListSubCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListSubCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListSubCodec.encodeResponse(((SerializableList)response).getCollection());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListSubCodec.RequestParameters)this.parameters).from, ((ListSubCodec.RequestParameters)this.parameters).to};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListSubCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "subList";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListSubCodec.RequestParameters)this.parameters).name;
    }
}

