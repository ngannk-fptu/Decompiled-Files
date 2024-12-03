/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.MemberAssigningJobProcessInformationImpl;

public class RequestMemberIdAssignment
extends ProcessingOperation {
    private volatile RequestPartitionResult result;

    public RequestMemberIdAssignment() {
    }

    public RequestMemberIdAssignment(String name, String jobId) {
        super(name, jobId);
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
        MemberAssigningJobProcessInformationImpl processInformation = this.getProcessInformation(supervisor);
        int memberId = processInformation.assignMemberId(this.getCallerAddress(), this.getCallerUuid(), supervisor.getConfiguration());
        if (memberId == -1) {
            this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.NO_MORE_PARTITIONS, -1);
            return;
        }
        this.result = new RequestPartitionResult(RequestPartitionResult.ResultState.SUCCESSFUL, memberId);
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 13;
    }

    private MemberAssigningJobProcessInformationImpl getProcessInformation(JobSupervisor supervisor) {
        return (MemberAssigningJobProcessInformationImpl)supervisor.getJobProcessInformation();
    }
}

