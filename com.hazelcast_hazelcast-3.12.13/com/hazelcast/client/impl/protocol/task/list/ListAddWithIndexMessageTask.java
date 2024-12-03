/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListAddWithIndexCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListAddOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListAddWithIndexMessageTask
extends AbstractPartitionMessageTask<ListAddWithIndexCodec.RequestParameters> {
    public ListAddWithIndexMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListAddOperation(((ListAddWithIndexCodec.RequestParameters)this.parameters).name, ((ListAddWithIndexCodec.RequestParameters)this.parameters).index, ((ListAddWithIndexCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected ListAddWithIndexCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListAddWithIndexCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListAddWithIndexCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListAddWithIndexCodec.RequestParameters)this.parameters).index, ((ListAddWithIndexCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListAddWithIndexCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "add";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListAddWithIndexCodec.RequestParameters)this.parameters).name;
    }
}

