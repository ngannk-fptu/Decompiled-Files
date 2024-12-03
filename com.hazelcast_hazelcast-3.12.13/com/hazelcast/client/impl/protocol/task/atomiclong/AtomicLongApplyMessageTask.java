/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongApplyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.ApplyOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongApplyMessageTask
extends AbstractPartitionMessageTask<AtomicLongApplyCodec.RequestParameters> {
    public AtomicLongApplyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        IFunction f = (IFunction)this.serializationService.toObject(((AtomicLongApplyCodec.RequestParameters)this.parameters).function);
        return new ApplyOperation(((AtomicLongApplyCodec.RequestParameters)this.parameters).name, f);
    }

    @Override
    protected AtomicLongApplyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongApplyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongApplyCodec.encodeResponse(this.nodeEngine.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongApplyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongApplyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "apply";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongApplyCodec.RequestParameters)this.parameters).function};
    }
}

