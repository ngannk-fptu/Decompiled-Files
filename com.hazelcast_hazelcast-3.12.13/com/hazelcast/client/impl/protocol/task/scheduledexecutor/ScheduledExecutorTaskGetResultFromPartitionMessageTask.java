/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetResultFromPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.operations.GetResultOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskGetResultFromPartitionMessageTask
extends AbstractPartitionMessageTask<ScheduledExecutorGetResultFromPartitionCodec.RequestParameters> {
    public ScheduledExecutorTaskGetResultFromPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(this.clientMessage.getPartitionId(), ((ScheduledExecutorGetResultFromPartitionCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorGetResultFromPartitionCodec.RequestParameters)this.parameters).taskName);
        return new GetResultOperation(handler);
    }

    @Override
    protected ScheduledExecutorGetResultFromPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorGetResultFromPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.nodeEngine.getSerializationService().toData(response);
        return ScheduledExecutorGetResultFromPartitionCodec.encodeResponse(data);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetResultFromPartitionCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetResultFromPartitionCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "getResultTimeout";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    protected void sendClientMessage(Throwable throwable) {
        if (throwable instanceof ScheduledTaskResult.ExecutionExceptionDecorator) {
            super.sendClientMessage(throwable.getCause());
        } else {
            super.sendClientMessage(throwable);
        }
    }
}

