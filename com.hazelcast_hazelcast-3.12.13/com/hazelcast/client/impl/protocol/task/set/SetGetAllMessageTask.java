/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetGetAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionGetAllOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;

public class SetGetAllMessageTask
extends AbstractPartitionMessageTask<SetGetAllCodec.RequestParameters> {
    public SetGetAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionGetAllOperation(((SetGetAllCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected SetGetAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetGetAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetGetAllCodec.encodeResponse(((SerializableList)response).getCollection());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetGetAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetGetAllCodec.RequestParameters)this.parameters).name;
    }
}

