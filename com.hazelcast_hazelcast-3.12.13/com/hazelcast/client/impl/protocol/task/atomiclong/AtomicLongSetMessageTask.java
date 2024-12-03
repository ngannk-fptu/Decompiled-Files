/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.SetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongSetMessageTask
extends AbstractPartitionMessageTask<AtomicLongSetCodec.RequestParameters> {
    public AtomicLongSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new SetOperation(((AtomicLongSetCodec.RequestParameters)this.parameters).name, ((AtomicLongSetCodec.RequestParameters)this.parameters).newValue);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongSetCodec.encodeResponse();
    }

    @Override
    protected AtomicLongSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongSetCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongSetCodec.RequestParameters)this.parameters).newValue};
    }
}

