/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.JobProcessInformation;
import com.hazelcast.mapreduce.impl.task.JobPartitionStateImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Address;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class JobProcessInformationImpl
implements JobProcessInformation {
    private static final AtomicReferenceFieldUpdater<JobProcessInformationImpl, JobPartitionState[]> PARTITION_STATE = AtomicReferenceFieldUpdater.newUpdater(JobProcessInformationImpl.class, JobPartitionState[].class, "partitionStates");
    private static final AtomicIntegerFieldUpdater<JobProcessInformationImpl> PROCESSED_RECORDS = AtomicIntegerFieldUpdater.newUpdater(JobProcessInformationImpl.class, "processedRecords");
    private final JobSupervisor supervisor;
    private volatile int processedRecords;
    private volatile JobPartitionState[] partitionStates;

    public JobProcessInformationImpl(int partitionCount, JobSupervisor supervisor) {
        this.supervisor = supervisor;
        this.partitionStates = new JobPartitionState[partitionCount];
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="Exposed array is used a lot on internals and is explicitly exposed for speed / object creation reasons. It is never exposed to the end user (either through serialization cycle or by hiding in through a wrapper class")
    public JobPartitionState[] getPartitionStates() {
        return this.partitionStates;
    }

    @Override
    public int getProcessedRecords() {
        return this.processedRecords;
    }

    public void addProcessedRecords(int records) {
        PROCESSED_RECORDS.addAndGet(this, records);
    }

    public void cancelPartitionState() {
        JobPartitionState[] oldPartitionStates = this.partitionStates;
        JobPartitionState[] newPartitionStates = new JobPartitionState[oldPartitionStates.length];
        for (int i = 0; i < newPartitionStates.length; ++i) {
            Address owner = oldPartitionStates[i] != null ? oldPartitionStates[i].getOwner() : null;
            newPartitionStates[i] = new JobPartitionStateImpl(owner, JobPartitionState.State.CANCELLED);
        }
        this.partitionStates = newPartitionStates;
    }

    public boolean updatePartitionState(int partitionId, JobPartitionState oldPartitionState, JobPartitionState newPartitionState) {
        JobPartitionState[] newPartitionStates;
        JobPartitionState[] oldPartitionStates;
        do {
            if ((oldPartitionStates = this.getPartitionStates())[partitionId] != oldPartitionState) {
                return false;
            }
            newPartitionStates = Arrays.copyOf(oldPartitionStates, oldPartitionStates.length);
            newPartitionStates[partitionId] = newPartitionState;
        } while (!this.updatePartitionState(oldPartitionStates, newPartitionStates));
        return true;
    }

    public boolean updatePartitionState(JobPartitionState[] oldPartitionStates, JobPartitionState[] newPartitionStates) {
        Preconditions.isNotNull(newPartitionStates, "newPartitionStates");
        if (oldPartitionStates.length != newPartitionStates.length) {
            throw new IllegalArgumentException("partitionStates need to have same length");
        }
        if (PARTITION_STATE.compareAndSet(this, oldPartitionStates, newPartitionStates)) {
            this.supervisor.checkFullyProcessed(this);
            return true;
        }
        return false;
    }

    public String toString() {
        return "JobProcessInformationImpl{processedRecords=" + this.processedRecords + ", partitionStates=" + Arrays.toString(this.partitionStates) + '}';
    }
}

