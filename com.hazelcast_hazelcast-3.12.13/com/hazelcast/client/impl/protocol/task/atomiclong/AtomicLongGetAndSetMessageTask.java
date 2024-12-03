/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.GetAndSetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongGetAndSetMessageTask
extends AbstractPartitionMessageTask<AtomicLongGetAndSetCodec.RequestParameters> {
    public AtomicLongGetAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetAndSetOperation(((AtomicLongGetAndSetCodec.RequestParameters)this.parameters).name, ((AtomicLongGetAndSetCodec.RequestParameters)this.parameters).newValue);
    }

    @Override
    protected AtomicLongGetAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongGetAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongGetAndSetCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongGetAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongGetAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongGetAndSetCodec.RequestParameters)this.parameters).newValue};
    }
}

