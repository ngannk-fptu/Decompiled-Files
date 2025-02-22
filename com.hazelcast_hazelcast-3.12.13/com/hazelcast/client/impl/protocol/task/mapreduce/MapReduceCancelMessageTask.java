/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceCancelCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import java.security.Permission;
import java.util.concurrent.CancellationException;

public class MapReduceCancelMessageTask
extends AbstractCallableMessageTask<MapReduceCancelCodec.RequestParameters>
implements BlockingMessageTask {
    public MapReduceCancelMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService("hz:impl:mapReduceService");
        Address jobOwner = mapReduceService.getLocalAddress();
        mapReduceService.registerJobSupervisorCancellation(((MapReduceCancelCodec.RequestParameters)this.parameters).name, ((MapReduceCancelCodec.RequestParameters)this.parameters).jobId, jobOwner);
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(((MapReduceCancelCodec.RequestParameters)this.parameters).name, ((MapReduceCancelCodec.RequestParameters)this.parameters).jobId);
        if (supervisor != null && supervisor.isOwnerNode()) {
            CancellationException exception = new CancellationException("Operation was cancelled by the user");
            supervisor.cancelAndNotify(exception);
        }
        return true;
    }

    @Override
    protected MapReduceCancelCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceCancelCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReduceCancelCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapReduceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReduceCancelCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

