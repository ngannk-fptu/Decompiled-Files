/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetStatsFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.GetStatisticsOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTaskGetStatisticsFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorGetStatsFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskGetStatisticsFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new GetStatisticsOperation(handler);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorGetStatsFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorGetStatsFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ScheduledTaskStatistics stats = (ScheduledTaskStatistics)response;
        return ScheduledExecutorGetStatsFromAddressCodec.encodeResponse(stats.getLastIdleTime(TimeUnit.NANOSECONDS), stats.getTotalIdleTime(TimeUnit.NANOSECONDS), stats.getTotalRuns(), stats.getTotalRunTime(TimeUnit.NANOSECONDS), stats.getLastRunDuration(TimeUnit.NANOSECONDS));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetStatsFromAddressCodec.RequestParameters)this.parameters).schedulerName;
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

