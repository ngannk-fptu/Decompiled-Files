/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListIndexOfCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListIndexOfOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListIndexOfMessageTask
extends AbstractPartitionMessageTask<ListIndexOfCodec.RequestParameters> {
    public ListIndexOfMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListIndexOfOperation(((ListIndexOfCodec.RequestParameters)this.parameters).name, false, ((ListIndexOfCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected ListIndexOfCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListIndexOfCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListIndexOfCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListIndexOfCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListIndexOfCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "indexOf";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListIndexOfCodec.RequestParameters)this.parameters).name;
    }
}

