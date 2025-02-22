/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.JobTaskConfiguration;
import com.hazelcast.nio.Address;

public class MemberAssigningJobProcessInformationImpl
extends JobProcessInformationImpl {
    public MemberAssigningJobProcessInformationImpl(int partitionCount, JobSupervisor supervisor) {
        super(partitionCount, supervisor);
    }

    public int assignMemberId(Address address, String memberUuid, JobTaskConfiguration configuration) {
        JobPartitionState[] partitionStates = this.getPartitionStates();
        for (int i = 0; i < partitionStates.length; ++i) {
            JobPartitionState partitionState = partitionStates[i];
            if (partitionState != null && partitionState.getState() != JobPartitionState.State.WAITING || MapReduceUtil.stateChange(address, i, JobPartitionState.State.WAITING, this, configuration) == null) continue;
            return i;
        }
        return -1;
    }
}

