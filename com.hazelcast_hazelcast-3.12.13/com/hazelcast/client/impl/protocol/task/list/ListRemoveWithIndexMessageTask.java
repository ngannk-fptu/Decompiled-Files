/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListRemoveWithIndexCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListRemoveWithIndexMessageTask
extends AbstractPartitionMessageTask<ListRemoveWithIndexCodec.RequestParameters> {
    public ListRemoveWithIndexMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListRemoveOperation(((ListRemoveWithIndexCodec.RequestParameters)this.parameters).name, ((ListRemoveWithIndexCodec.RequestParameters)this.parameters).index);
    }

    @Override
    protected ListRemoveWithIndexCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListRemoveWithIndexCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListRemoveWithIndexCodec.encodeResponse((Data)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListRemoveWithIndexCodec.RequestParameters)this.parameters).index};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListRemoveWithIndexCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListRemoveWithIndexCodec.RequestParameters)this.parameters).name;
    }
}

