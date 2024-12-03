/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import java.util.Map;

public class GetResultOperation
extends ProcessingOperation {
    private volatile Map result;

    public GetResultOperation() {
    }

    public GetResultOperation(String name, String jobId) {
        super(name, jobId);
    }

    public Map getResult() {
        return this.result;
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor != null) {
            this.result = supervisor.getJobResults();
            if (!supervisor.isOwnerNode()) {
                mapReduceService.destroyJobSupervisor(supervisor);
                AbstractJobTracker jobTracker = (AbstractJobTracker)mapReduceService.getJobTracker(this.getName());
                jobTracker.unregisterTrackableJob(this.getJobId());
                jobTracker.unregisterMapCombineTask(this.getJobId());
                jobTracker.unregisterReducerTask(this.getJobId());
            }
        }
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }
}

