/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionSizeOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SetSizeMessageTask
extends AbstractPartitionMessageTask<SetSizeCodec.RequestParameters> {
    public SetSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionSizeOperation(((SetSizeCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected SetSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetSizeCodec.encodeResponse((Integer)response);
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
        return new SetPermission(((SetSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "size";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetSizeCodec.RequestParameters)this.parameters).name;
    }
}

