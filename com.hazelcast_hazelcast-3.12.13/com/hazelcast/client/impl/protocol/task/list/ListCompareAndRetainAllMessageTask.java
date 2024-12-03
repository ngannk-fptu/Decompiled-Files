/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListCompareAndRetainAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionCompareAndRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.HashSet;

public class ListCompareAndRetainAllMessageTask
extends AbstractPartitionMessageTask<ListCompareAndRetainAllCodec.RequestParameters> {
    public ListCompareAndRetainAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        HashSet<Data> values = new HashSet<Data>(((ListCompareAndRetainAllCodec.RequestParameters)this.parameters).values);
        return new CollectionCompareAndRemoveOperation(((ListCompareAndRetainAllCodec.RequestParameters)this.parameters).name, true, values);
    }

    @Override
    protected ListCompareAndRetainAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListCompareAndRetainAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListCompareAndRetainAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListCompareAndRetainAllCodec.RequestParameters)this.parameters).values};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListCompareAndRetainAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "retainAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListCompareAndRetainAllCodec.RequestParameters)this.parameters).name;
    }
}

