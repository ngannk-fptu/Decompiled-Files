/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorCancelFromPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.CancelTaskOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskCancelFromPartitionMessageTask
extends AbstractPartitionMessageTask<ScheduledExecutorCancelFromPartitionCodec.RequestParameters> {
    public ScheduledExecutorTaskCancelFromPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(this.clientMessage.getPartitionId(), ((ScheduledExecutorCancelFromPartitionCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorCancelFromPartitionCodec.RequestParameters)this.parameters).taskName);
        return new CancelTaskOperation(handler, ((ScheduledExecutorCancelFromPartitionCodec.RequestParameters)this.parameters).mayInterruptIfRunning);
    }

    @Override
    protected ScheduledExecutorCancelFromPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorCancelFromPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorCancelFromPartitionCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorCancelFromPartitionCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorCancelFromPartitionCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "cancel";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ScheduledExecutorCancelFromPartitionCodec.RequestParameters)this.parameters).mayInterruptIfRunning};
    }
}

