/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.GetOperation;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ReplicatedMapGetMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapGetCodec.RequestParameters> {
    private volatile long startTimeNanos;

    public ReplicatedMapGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetOperation(((ReplicatedMapGetCodec.RequestParameters)this.parameters).name, ((ReplicatedMapGetCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected ReplicatedMapGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected void beforeProcess() {
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    protected void beforeResponse() {
        long latencyNanos = System.nanoTime() - this.startTimeNanos;
        ReplicatedMapService replicatedMapService = (ReplicatedMapService)this.getService("hz:impl:replicatedMapService");
        if (replicatedMapService.getReplicatedMapConfig(((ReplicatedMapGetCodec.RequestParameters)this.parameters).name).isStatisticsEnabled()) {
            replicatedMapService.getLocalMapStatsImpl(((ReplicatedMapGetCodec.RequestParameters)this.parameters).name).incrementGets(latencyNanos);
        }
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapGetCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ReplicatedMapGetCodec.RequestParameters)this.parameters).key};
    }
}

