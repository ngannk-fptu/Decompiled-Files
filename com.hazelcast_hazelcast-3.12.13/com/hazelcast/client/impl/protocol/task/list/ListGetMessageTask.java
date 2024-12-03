/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListGetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListGetMessageTask
extends AbstractPartitionMessageTask<ListGetCodec.RequestParameters> {
    public ListGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListGetOperation(((ListGetCodec.RequestParameters)this.parameters).name, ((ListGetCodec.RequestParameters)this.parameters).index);
    }

    @Override
    protected ListGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListGetCodec.encodeResponse((Data)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListGetCodec.RequestParameters)this.parameters).index};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListGetCodec.RequestParameters)this.parameters).name;
    }
}

