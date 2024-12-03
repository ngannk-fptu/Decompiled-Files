/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetStatsFromPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.GetStatisticsOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTaskGetStatisticsFromPartitionMessageTask
extends AbstractPartitionMessageTask<ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters> {
    public ScheduledExecutorTaskGetStatisticsFromPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(this.clientMessage.getPartitionId(), ((ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters)this.parameters).taskName);
        return new GetStatisticsOperation(handler);
    }

    @Override
    protected ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorGetStatsFromPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ScheduledTaskStatistics stats = (ScheduledTaskStatistics)response;
        return ScheduledExecutorGetStatsFromPartitionCodec.encodeResponse(stats.getLastIdleTime(TimeUnit.NANOSECONDS), stats.getTotalIdleTime(TimeUnit.NANOSECONDS), stats.getTotalRuns(), stats.getTotalRunTime(TimeUnit.NANOSECONDS), stats.getLastRunDuration(TimeUnit.NANOSECONDS));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "getStatistics";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

