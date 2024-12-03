/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceJobProcessInformationCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Connection;
import java.security.Permission;
import java.util.Arrays;
import java.util.List;

public class MapReduceJobProcessInformationMessageTask
extends AbstractCallableMessageTask<MapReduceJobProcessInformationCodec.RequestParameters> {
    public MapReduceJobProcessInformationMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService("hz:impl:mapReduceService");
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(((MapReduceJobProcessInformationCodec.RequestParameters)this.parameters).name, ((MapReduceJobProcessInformationCodec.RequestParameters)this.parameters).jobId);
        if (supervisor != null && supervisor.getJobProcessInformation() != null) {
            JobProcessInformationImpl current = supervisor.getJobProcessInformation();
            List<JobPartitionState> jobPartitionStates = Arrays.asList(current.getPartitionStates());
            return MapReduceJobProcessInformationCodec.encodeResponse(jobPartitionStates, current.getProcessedRecords());
        }
        throw new IllegalStateException("Information not found for map reduce with name: " + ((MapReduceJobProcessInformationCodec.RequestParameters)this.parameters).name + ", job ID: " + ((MapReduceJobProcessInformationCodec.RequestParameters)this.parameters).jobId);
    }

    @Override
    protected MapReduceJobProcessInformationCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceJobProcessInformationCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return (ClientMessage)response;
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
        return ((MapReduceJobProcessInformationCodec.RequestParameters)this.parameters).name;
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

