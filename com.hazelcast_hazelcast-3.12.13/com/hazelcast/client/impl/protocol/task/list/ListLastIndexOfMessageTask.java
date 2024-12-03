/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListLastIndexOfCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListIndexOfOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListLastIndexOfMessageTask
extends AbstractPartitionMessageTask<ListLastIndexOfCodec.RequestParameters> {
    public ListLastIndexOfMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListIndexOfOperation(((ListLastIndexOfCodec.RequestParameters)this.parameters).name, true, ((ListLastIndexOfCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected ListLastIndexOfCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListLastIndexOfCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListLastIndexOfCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListLastIndexOfCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListLastIndexOfCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "lastIndexOf";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListLastIndexOfCodec.RequestParameters)this.parameters).name;
    }
}

