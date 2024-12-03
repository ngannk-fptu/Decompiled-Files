/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListContainsAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionContainsOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.HashSet;

public class ListContainsAllMessageTask
extends AbstractPartitionMessageTask<ListContainsAllCodec.RequestParameters> {
    public ListContainsAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        HashSet<Data> values = new HashSet<Data>(((ListContainsAllCodec.RequestParameters)this.parameters).values);
        return new CollectionContainsOperation(((ListContainsAllCodec.RequestParameters)this.parameters).name, values);
    }

    @Override
    protected ListContainsAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListContainsAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListContainsAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListContainsAllCodec.RequestParameters)this.parameters).values};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListContainsAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "containsAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListContainsAllCodec.RequestParameters)this.parameters).name;
    }
}

