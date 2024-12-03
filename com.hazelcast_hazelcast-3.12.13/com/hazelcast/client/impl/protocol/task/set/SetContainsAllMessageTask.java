/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetContainsAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionContainsOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.HashSet;

public class SetContainsAllMessageTask
extends AbstractPartitionMessageTask<SetContainsAllCodec.RequestParameters> {
    public SetContainsAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        HashSet<Data> values = new HashSet<Data>(((SetContainsAllCodec.RequestParameters)this.parameters).items);
        return new CollectionContainsOperation(((SetContainsAllCodec.RequestParameters)this.parameters).name, values);
    }

    @Override
    protected SetContainsAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetContainsAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetContainsAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SetContainsAllCodec.RequestParameters)this.parameters).items};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetContainsAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "containsAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetContainsAllCodec.RequestParameters)this.parameters).name;
    }
}

