/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.JobProcessInformation;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.spi.partition.IPartitionService;
import java.util.List;

public class RequestPartitionMapping
extends ProcessingOperation {
    private volatile RequestPartitionResult result;

    public RequestPartitionMapping() {
    }

    public RequestPartitionMapping(String name, String jobId) {
        super(name, jobId);
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public void run() throws Exception {
        int selectedPartition;
        JobPartitionState.State nextState;
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor == null) {
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.NO_SUPERVISOR, -1);
            return;
        }
        IPartitionService ps = this.getNodeEngine().getPartitionService();
        List<Integer> memberPartitions = ps.getMemberPartitions(this.getCallerAddress());
        JobProcessInformationImpl processInformation = supervisor.getJobProcessInformation();
        do {
            if ((selectedPartition = this.searchMemberPartitionToProcess(processInformation, memberPartitions)) != -1) continue;
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.NO_MORE_PARTITIONS, -1);
            return;
        } while ((nextState = MapReduceUtil.stateChange(this.getCallerAddress(), selectedPartition, JobPartitionState.State.WAITING, processInformation, supervisor.getConfiguration())) != JobPartitionState.State.MAPPING);
        this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.SUCCESSFUL, selectedPartition);
    }

    private int searchMemberPartitionToProcess(JobProcessInformation processInformation, List<Integer> memberPartitions) {
        for (int partitionId : memberPartitions) {
            if (!this.checkState(processInformation, partitionId)) continue;
            return partitionId;
        }
        return -1;
    }

    private boolean checkState(JobProcessInformation processInformation, int partitionId) {
        JobPartitionState[] partitionStates = processInformation.getPartitionStates();
        JobPartitionState partitionState = partitionStates[partitionId];
        return partitionState == null || partitionState.getState() == JobPartitionState.State.WAITING;
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }
}

