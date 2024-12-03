/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListCompareAndRemoveAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionCompareAndRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.HashSet;

public class ListCompareAndRemoveAllMessageTask
extends AbstractPartitionMessageTask<ListCompareAndRemoveAllCodec.RequestParameters> {
    public ListCompareAndRemoveAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        HashSet<Data> values = new HashSet<Data>(((ListCompareAndRemoveAllCodec.RequestParameters)this.parameters).values);
        return new CollectionCompareAndRemoveOperation(((ListCompareAndRemoveAllCodec.RequestParameters)this.parameters).name, false, values);
    }

    @Override
    protected ListCompareAndRemoveAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListCompareAndRemoveAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListCompareAndRemoveAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ListCompareAndRemoveAllCodec.RequestParameters)this.parameters).values};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListCompareAndRemoveAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "removeAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListCompareAndRemoveAllCodec.RequestParameters)this.parameters).name;
    }
}

