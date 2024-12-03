/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListAddAllWithIndexCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListAddAllOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ListAddAllWithIndexMessageTask
extends AbstractPartitionMessageTask<ListAddAllWithIndexCodec.RequestParameters> {
    public ListAddAllWithIndexMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListAddAllOperation(((ListAddAllWithIndexCodec.RequestParameters)this.parameters).name, ((ListAddAllWithIndexCodec.RequestParameters)this.parameters).index, ((ListAddAllWithIndexCodec.RequestParameters)this.parameters).valueList);
    }

    @Override
    protected ListAddAllWithIndexCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListAddAllWithIndexCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListAddAllWithIndexCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListAddAllWithIndexCodec.RequestParameters)this.parameters).index, ((ListAddAllWithIndexCodec.RequestParameters)this.parameters).valueList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListAddAllWithIndexCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "addAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListAddAllWithIndexCodec.RequestParameters)this.parameters).name;
    }
}

