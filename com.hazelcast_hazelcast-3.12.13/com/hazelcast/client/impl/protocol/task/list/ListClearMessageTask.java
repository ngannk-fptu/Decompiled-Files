/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListClearCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionClearOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListClearMessageTask
extends AbstractPartitionMessageTask<ListClearCodec.RequestParameters> {
    public ListClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionClearOperation(((ListClearCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ListClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListClearCodec.encodeResponse();
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
        return new ListPermission(((ListClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListClearCodec.RequestParameters)this.parameters).name;
    }
}

