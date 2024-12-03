/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetIsEmptyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionIsEmptyOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SetIsEmptyMessageTask
extends AbstractPartitionMessageTask<SetIsEmptyCodec.RequestParameters> {
    public SetIsEmptyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionIsEmptyOperation(((SetIsEmptyCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected SetIsEmptyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetIsEmptyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetIsEmptyCodec.encodeResponse((Boolean)response);
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
        return new SetPermission(((SetIsEmptyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "isEmpty";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetIsEmptyCodec.RequestParameters)this.parameters).name;
    }
}

