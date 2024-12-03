/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class RequestPartitionProcessed
extends ProcessingOperation {
    private volatile RequestPartitionResult result;
    private int partitionId;
    private JobPartitionState.State currentState;

    public RequestPartitionProcessed() {
    }

    public RequestPartitionProcessed(String name, String jobId, int partitionId, JobPartitionState.State currentState) {
        super(name, jobId);
        this.partitionId = partitionId;
        this.currentState = currentState;
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor == null) {
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.NO_SUPERVISOR, -1);
            return;
        }
        JobProcessInformationImpl processInformation = supervisor.getJobProcessInformation();
        JobPartitionState.State nextState = MapReduceUtil.stateChange(this.getCallerAddress(), this.partitionId, this.currentState, processInformation, supervisor.getConfiguration());
        if (nextState == JobPartitionState.State.PROCESSED) {
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.SUCCESSFUL, this.partitionId);
            return;
        }
        this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.CHECK_STATE_FAILED, -1);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.partitionId);
        out.writeInt(this.currentState.ordinal());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.partitionId = in.readInt();
        this.currentState = JobPartitionState.State.byOrdinal(in.readInt());
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }
}

