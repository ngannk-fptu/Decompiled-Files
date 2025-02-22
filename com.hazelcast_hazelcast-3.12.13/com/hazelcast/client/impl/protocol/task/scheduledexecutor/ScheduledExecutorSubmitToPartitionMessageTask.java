/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorSubmitToPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.ScheduleTaskOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorSubmitToPartitionMessageTask
extends AbstractPartitionMessageTask<ScheduledExecutorSubmitToPartitionCodec.RequestParameters> {
    public ScheduledExecutorSubmitToPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Callable callable = (Callable)this.serializationService.toObject(((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).task);
        TaskDefinition def = new TaskDefinition(TaskDefinition.Type.getById(((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).type), ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).taskName, callable, ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).initialDelayInMillis, ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).periodInMillis, TimeUnit.MILLISECONDS);
        return new ScheduleTaskOperation(((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).schedulerName, def);
    }

    @Override
    protected ScheduledExecutorSubmitToPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorSubmitToPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorSubmitToPartitionCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "submitToPartition";
    }

    @Override
    public Object[] getParameters() {
        Callable callable = (Callable)this.serializationService.toObject(((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).task);
        TaskDefinition def = new TaskDefinition(TaskDefinition.Type.getById(((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).type), ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).taskName, callable, ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).initialDelayInMillis, ((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).periodInMillis, TimeUnit.MILLISECONDS);
        return new Object[]{((ScheduledExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).schedulerName, def};
    }
}

