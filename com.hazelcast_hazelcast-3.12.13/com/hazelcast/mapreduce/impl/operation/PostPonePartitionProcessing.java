/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class PostPonePartitionProcessing
extends ProcessingOperation {
    private volatile RequestPartitionResult result;
    private int partitionId;

    public PostPonePartitionProcessing() {
    }

    public PostPonePartitionProcessing(String name, String jobId, int partitionId) {
        super(name, jobId);
        this.partitionId = partitionId;
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public void run() throws Exception {
        JobPartitionState oldPartitionState;
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor == null) {
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.NO_SUPERVISOR, -1);
            return;
        }
        JobProcessInformationImpl processInformation = supervisor.getJobProcessInformation();
        do {
            JobPartitionState[] partitionStates;
            if ((oldPartitionState = (partitionStates = processInformation.getPartitionStates())[this.partitionId]) != null && this.getCallerAddress().equals(oldPartitionState.getOwner())) continue;
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.CHECK_STATE_FAILED, this.partitionId);
            return;
        } while (!processInformation.updatePartitionState(this.partitionId, oldPartitionState, null));
        this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.SUCCESSFUL, this.partitionId);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.partitionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.partitionId = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }
}

