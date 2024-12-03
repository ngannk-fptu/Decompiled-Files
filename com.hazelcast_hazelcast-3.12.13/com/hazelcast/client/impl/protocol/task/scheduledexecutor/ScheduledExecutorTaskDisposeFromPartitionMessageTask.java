/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorDisposeFromPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.DisposeTaskOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskDisposeFromPartitionMessageTask
extends AbstractPartitionMessageTask<ScheduledExecutorDisposeFromPartitionCodec.RequestParameters> {
    public ScheduledExecutorTaskDisposeFromPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(this.clientMessage.getPartitionId(), ((ScheduledExecutorDisposeFromPartitionCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorDisposeFromPartitionCodec.RequestParameters)this.parameters).taskName);
        return new DisposeTaskOperation(handler);
    }

    @Override
    protected ScheduledExecutorDisposeFromPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorDisposeFromPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorDisposeFromPartitionCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorDisposeFromPartitionCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorDisposeFromPartitionCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "dispose";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

