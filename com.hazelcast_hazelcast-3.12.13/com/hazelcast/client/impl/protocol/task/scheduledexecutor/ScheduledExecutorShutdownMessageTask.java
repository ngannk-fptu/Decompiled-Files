/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorShutdownCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.impl.operations.ShutdownOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;

public class ScheduledExecutorShutdownMessageTask
extends AbstractInvocationMessageTask<ScheduledExecutorShutdownCodec.RequestParameters> {
    public ScheduledExecutorShutdownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).address);
    }

    @Override
    protected Operation prepareOperation() {
        ShutdownOperation op = new ShutdownOperation(((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).schedulerName);
        op.setCallerUuid(this.endpoint.getUuid());
        return op;
    }

    @Override
    protected ScheduledExecutorShutdownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorShutdownCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorShutdownCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorShutdownCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "shutdown";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ScheduledExecutorShutdownCodec.RequestParameters)this.parameters).schedulerName};
    }
}

