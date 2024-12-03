/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.JobPartitionState;

@Deprecated
public interface JobProcessInformation {
    public JobPartitionState[] getPartitionStates();

    public int getProcessedRecords();
}

