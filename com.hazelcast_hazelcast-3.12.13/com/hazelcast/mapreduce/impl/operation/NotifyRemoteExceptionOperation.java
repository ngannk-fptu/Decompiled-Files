/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class NotifyRemoteExceptionOperation
extends ProcessingOperation {
    private Throwable throwable;

    public NotifyRemoteExceptionOperation() {
    }

    public NotifyRemoteExceptionOperation(String name, String jobId, Throwable throwable) {
        super(name, jobId);
        this.throwable = throwable;
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor != null) {
            supervisor.notifyRemoteException(this.getCallerAddress(), this.throwable);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.throwable);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.throwable = (Throwable)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 15;
    }
}

