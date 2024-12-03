/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetDelayFromPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.GetDelayOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTaskGetDelayFromPartitionMessageTask
extends AbstractPartitionMessageTask<ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters> {
    public ScheduledExecutorTaskGetDelayFromPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(this.clientMessage.getPartitionId(), ((ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters)this.parameters).taskName);
        return new GetDelayOperation(handler, TimeUnit.NANOSECONDS);
    }

    @Override
    protected ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorGetDelayFromPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorGetDelayFromPartitionCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "getDelay";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{TimeUnit.NANOSECONDS};
    }
}

