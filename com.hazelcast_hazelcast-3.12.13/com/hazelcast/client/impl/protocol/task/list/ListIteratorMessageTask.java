/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListIteratorCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListSubOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;

public class ListIteratorMessageTask
extends AbstractPartitionMessageTask<ListIteratorCodec.RequestParameters> {
    public ListIteratorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListSubOperation(((ListIteratorCodec.RequestParameters)this.parameters).name, -1, -1);
    }

    @Override
    protected ListIteratorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListIteratorCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListIteratorCodec.encodeResponse(((SerializableList)response).getCollection());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListIteratorCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListIteratorCodec.RequestParameters)this.parameters).name;
    }
}

