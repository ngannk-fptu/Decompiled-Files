/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetClearCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionClearOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SetClearMessageTask
extends AbstractPartitionMessageTask<SetClearCodec.RequestParameters> {
    public SetClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionClearOperation(((SetClearCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected SetClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetClearCodec.encodeResponse();
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
        return new SetPermission(((SetClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetClearCodec.RequestParameters)this.parameters).name;
    }
}

