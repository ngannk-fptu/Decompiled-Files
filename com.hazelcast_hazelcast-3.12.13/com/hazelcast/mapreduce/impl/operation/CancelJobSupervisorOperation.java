/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class CancelJobSupervisorOperation
extends ProcessingOperation {
    private Address jobOwner;

    public CancelJobSupervisorOperation() {
    }

    public CancelJobSupervisorOperation(String name, String jobId) {
        super(name, jobId);
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        mapReduceService.registerJobSupervisorCancellation(this.getName(), this.getJobId(), this.jobOwner);
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor != null) {
            supervisor.cancel();
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.jobOwner);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.jobOwner = (Address)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }
}

