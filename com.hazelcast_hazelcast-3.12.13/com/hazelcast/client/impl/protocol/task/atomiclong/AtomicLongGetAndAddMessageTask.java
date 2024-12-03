/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndAddCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.GetAndAddOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongGetAndAddMessageTask
extends AbstractPartitionMessageTask<AtomicLongGetAndAddCodec.RequestParameters> {
    public AtomicLongGetAndAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetAndAddOperation(((AtomicLongGetAndAddCodec.RequestParameters)this.parameters).name, ((AtomicLongGetAndAddCodec.RequestParameters)this.parameters).delta);
    }

    @Override
    protected AtomicLongGetAndAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongGetAndAddCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongGetAndAddCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongGetAndAddCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongGetAndAddCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndAdd";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongGetAndAddCodec.RequestParameters)this.parameters).delta};
    }
}

