/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListSetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListSetMessageTask
extends AbstractPartitionMessageTask<ListSetCodec.RequestParameters> {
    public ListSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListSetOperation(((ListSetCodec.RequestParameters)this.parameters).name, ((ListSetCodec.RequestParameters)this.parameters).index, ((ListSetCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected ListSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListSetCodec.encodeResponse((Data)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListSetCodec.RequestParameters)this.parameters).index, ((ListSetCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListSetCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListSetCodec.RequestParameters)this.parameters).name;
    }
}

