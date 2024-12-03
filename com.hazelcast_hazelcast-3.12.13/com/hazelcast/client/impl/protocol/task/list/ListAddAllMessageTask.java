/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListAddAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionAddAllOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListAddAllMessageTask
extends AbstractPartitionMessageTask<ListAddAllCodec.RequestParameters> {
    public ListAddAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionAddAllOperation(((ListAddAllCodec.RequestParameters)this.parameters).name, ((ListAddAllCodec.RequestParameters)this.parameters).valueList);
    }

    @Override
    protected ListAddAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListAddAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListAddAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListAddAllCodec.RequestParameters)this.parameters).valueList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListAddAllCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "addAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListAddAllCodec.RequestParameters)this.parameters).name;
    }
}

